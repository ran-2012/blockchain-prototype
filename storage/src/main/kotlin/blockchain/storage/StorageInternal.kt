package blockchain.storage

import blockchain.data.core.Block
import blockchain.data.core.Transaction
import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.conversions.Bson
import org.jetbrains.annotations.TestOnly

class StorageInternal(dbName: String) : IStorage {

    private val client: DataBaseClient
    private val redisClient: RedisClient

    init {
        client = DataBaseClient(dbName)
        runBlocking {
            client.initCollection()
            client.initSchema()
        }

        redisClient = RedisClient(dbName)
    }

    private fun updateBlockMap(map: HashMap<Long, ArrayList<Block>>, block: Block) {
        val height = block.height
        if (map.containsKey(height)) {
            map[height]?.add(block)
        } else {
            val list = ArrayList<Block>()
            list.add(block)
            map[height] = list
        }
    }

    private fun getRangeFilter(heightMin: Long, heightMax: Long): Bson {
        return Filters.and(
            Filters.gte(DataBaseClient.FIELD_HEIGHT, heightMin),
            Filters.lte(DataBaseClient.FIELD_HEIGHT, heightMax)
        )
    }

    private fun getHashFilter(hash: String): Bson {
        return Filters.eq(DataBaseClient.FIELD_HASH, hash)
    }

    @TestOnly
    fun cleanUp() {
        runBlocking {
            client.block().deleteMany(Document())
            val keys = redisClient.getClient().keys(redisClient.dataBaseName + "*")
            for (key in keys) {
                redisClient.remove(key)
            }
        }
    }

    override fun addBlockSync(data: Block) {
        runBlocking {
            client.block().insertOne(data)
        }
    }

    override fun removeBlockSync(height: Long) {
        runBlocking {
            client.block().deleteMany(Filters.eq(DataBaseClient.FIELD_HEIGHT, height))
        }
    }

    override fun removeBlockRangeSync(heightMin: Long, heightMax: Long) {
        runBlocking {
            client.block().deleteMany(getRangeFilter(heightMin, heightMax))
        }
    }

    override fun getBlockAllSync(): Map<Long, List<Block>> {
        val map = HashMap<Long, ArrayList<Block>>()
        runBlocking {
            client.block().find().collect { block ->
                updateBlockMap(map, block)
            }
        }
        return map
    }

    override fun getBlockRangeSync(heightMin: Long, heightMax: Long): Map<Long, List<Block>> {
        val map = HashMap<Long, ArrayList<Block>>()
        runBlocking {
            client.block().find(getRangeFilter(heightMin, heightMax)).collect {
                updateBlockMap(map, it)
            }
        }
        return map
    }

    override fun getBlockSync(hash: String): Block {
        return runBlocking {
            client.block().find(getHashFilter(hash)).single()
        }
    }

    override fun addTransactionSync(data: Transaction) {
        runBlocking {
            client.transaction().insertOne(data)
        }
    }

    override fun removeTransactionSync(hash: String) {
        runBlocking {
            client.transaction().deleteMany(getHashFilter(hash))
        }
    }

    override fun getTransactionSync(sourceAddress: String): MutableList<Transaction> {
        val list = ArrayList<Transaction>()
        runBlocking {
            client.transaction().find(Filters.eq(DataBaseClient.FIELD_SOURCE_ADDRESS, sourceAddress)).collect {
                list.add(it)
            }
        }
        return list
    }

    override fun addUtxoSync(address: String, data: Transaction) {
        redisClient.addUtxo(address, data)
    }

    override fun removeUtxoSync(address: String, data: Transaction) {
        redisClient.removeUtxo(address, data)
    }

    override fun getUtxoListSync(address: String): Set<String> {
        return redisClient.getUtxo(address)
    }

    override fun getAddress(): Set<String> {
        return redisClient.getAddress()
    }
}