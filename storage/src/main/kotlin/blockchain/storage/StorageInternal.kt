package blockchain.storage

import blockchain.data.core.Block
import blockchain.data.core.Transaction
import blockchain.data.core.Utxo
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.conversions.Bson
import org.jetbrains.annotations.TestOnly
import java.lang.Exception

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
            redisClient.cleanUp()
        }
    }

    override fun addBlock(data: Block) {
        runBlocking {
            client.block().insertOne(data)
        }
    }

    override fun removeBlockByHeight(height: Long) {
        runBlocking {
            client.block().deleteMany(Filters.eq(DataBaseClient.FIELD_HEIGHT, height))
        }
    }

    override fun removeBlockByHash(hash: String) {
        runBlocking {
            client.block().deleteOne(Filters.eq(DataBaseClient.FIELD_HASH, hash))
        }
    }

    override fun removeBlockByHashRange(hashes: List<String>) {
        runBlocking {
            client.block().deleteMany(Filters.`in`(DataBaseClient.FIELD_HASH, hashes))
        }
    }

    override fun removeBlockByHeightRange(heightMin: Long, heightMax: Long) {
        runBlocking {
            client.block().deleteMany(getRangeFilter(heightMin, heightMax))
        }
    }

    override fun getBlockAll(): Map<Long, List<Block>> {
        val map = HashMap<Long, ArrayList<Block>>()
        runBlocking {
            client.block().find().collect { block ->
                updateBlockMap(map, block)
            }
        }
        return map
    }

    override fun getBlockRange(heightMin: Long, heightMax: Long): Map<Long, List<Block>> {
        val map = HashMap<Long, ArrayList<Block>>()
        runBlocking {
            client.block().find(getRangeFilter(heightMin, heightMax)).collect {
                updateBlockMap(map, it)
            }
        }
        return map
    }

    override fun getBlock(hash: String): Block? {
        return runBlocking {
            try {
                client.block().find(getHashFilter(hash)).single()
            } catch (ignored: Exception) {
                null
            }
        }
    }

    override fun getLastBlock(): MutableList<Block> {
        val list = ArrayList<Block>()
        runBlocking {
            try {
                val block = client.block().find(Sorts.descending(DataBaseClient.FIELD_HEIGHT)).limit(1).single()
                client.block().find(Filters.eq(DataBaseClient.FIELD_HEIGHT, block.height)).collect {
                    list.add(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return list
    }

    override fun addTransaction(data: Transaction) {
        runBlocking {
            client.transaction().insertOne(data)
        }
    }

    override fun removeTransaction(hash: String) {
        runBlocking {
            client.transaction().deleteMany(getHashFilter(hash))
        }
    }

    override fun getTransaction(sourceAddress: String): MutableList<Transaction> {
        val list = ArrayList<Transaction>()
        runBlocking {
            client.transaction().find(Filters.eq(DataBaseClient.FIELD_SOURCE_ADDRESS, sourceAddress)).collect {
                list.add(it)
            }
        }
        return list
    }

    override fun addUtxo(data: Utxo) {
        redisClient.addUtxo(data)
    }

    override fun removeUtxo(data: Utxo) {
        redisClient.removeUtxo(data)
    }

    override fun getUtxoByAddress(address: String): Set<Utxo> {
        return redisClient.getUtxo(address)
    }

    override fun getAddressAll(): Set<String> {
        return redisClient.getAddressAll()
    }

    override fun getUtxoAll(): Set<Utxo> {
        return redisClient.getUtxoAll()
    }

    override fun setHeight(height: Long) {
        redisClient.setHeight(height)
    }

    override fun getHeight(): Long {
        return redisClient.getHeight()
    }
}