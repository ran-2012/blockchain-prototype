package blockchain.storage

import blockchain.data.core.Block
import blockchain.data.core.Transaction
import blockchain.data.core.TransactionInput
import blockchain.data.core.TransactionInputOutputBase
import blockchain.data.core.TransactionOutput
import blockchain.utility.Log
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.runBlocking
import org.bson.Document
import org.bson.conversions.Bson
import org.jetbrains.annotations.Nullable
import org.jetbrains.annotations.TestOnly
import java.lang.Exception

class StorageInternal(dbName: String) : IStorage {

    private val log = Log.get(this)

    private val client: DataBaseClient
    private val redisClient: RedisClient

    private val lastBlockLock = Object()
    private var _lastBlock: Block? = null

    init {
        client = DataBaseClient(dbName)
        runBlocking {
            client.initCollection()
            client.initSchema()
        }

        redisClient = RedisClient(dbName)
    }

    private fun updateBlockMap(map: HashMap<Long, Block>, block: Block) {
        map[block.height] = block
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
        synchronized(lastBlockLock) {
            _lastBlock = null
            runBlocking {
                client.block().insertOne(data)
            }
        }
    }

    override fun removeBlock() {
        synchronized(lastBlockLock) {
            runBlocking {
                client.block().deleteOne(Filters.eq(DataBaseClient.FIELD_HEIGHT, height))
            }
            _lastBlock = null
        }
    }

    override fun getBlockAll(): MutableMap<Long, Block> {
        val map = HashMap<Long, Block>()
        runBlocking {
            client.block().find().collect {
                updateBlockMap(map, it)
            }
        }
        return map
    }

    override fun getBlockRange(heightMin: Long, heightMax: Long): MutableMap<Long, Block> {
        val map = HashMap<Long, Block>()
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

    @Nullable
    override fun getLastBlock(): Block? {
        return synchronized(lastBlockLock) {
            if (_lastBlock == null) {
                runBlocking {
                    try {
                        _lastBlock =
                            client.block().find().sort(Sorts.descending(DataBaseClient.FIELD_HEIGHT)).limit(1).single()
                    } catch (ignored: Exception) {
                        null
                    }
                }
            }
            _lastBlock
        }
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

    override fun getTransaction(hash: String): Transaction? {
        return runBlocking {
            client.transaction().find(Filters.eq(DataBaseClient.FIELD_HASH, hash)).singleOrNull()
        }
    }

    override fun getTransactionAll(): MutableList<Transaction> {
        val list = ArrayList<Transaction>()
        runBlocking {
            client.transaction().find().collect {
                list.add(it)
            }
        }
        return list
    }

    @TestOnly
    fun addUtxo(transactionId: String, outputIdx: Int, data: TransactionOutput) {
        redisClient.normal.addUtxo(transactionId, outputIdx, data)
    }

    @TestOnly
    fun removeUtxo(address: String, transactionId: String, outputIdx: Int) {
        redisClient.normal.removeUtxo(address, transactionId, outputIdx)
    }

    override fun addUtxoFromTransactionInput(transaction: Transaction) {
        transaction.inputs.forEach { input ->
            redisClient.normal.addUtxo(
                input.originalTxHash,
                input.originalOutputIndex,
                TransactionOutput(input.address, input.value)
            )
        }
    }

    override fun addUtxoFromTransactionOutput(transaction: Transaction) {
        transaction.outputs.forEachIndexed { i, output ->
            addUtxo(transaction.hash, i, output)
        }
    }

    override fun removeUtxoFromTransactionInput(transaction: Transaction) {
        val address = transaction.sourceAddress
        transaction.inputs.forEach { input ->
            removeUtxo(address, input.originalTxHash, input.originalOutputIndex)
        }
    }

    override fun removeUtxoFromTransactionOutput(transaction: Transaction) {
        transaction.outputs.forEachIndexed { i, output ->
            removeUtxo(output.address, transaction.hash, i)
        }
    }

    override fun hasUtxo(utxo: TransactionInput): Boolean {
        return redisClient.normal.existsUtxo("${utxo.originalTxHash}:${utxo.originalOutputIndex}")
    }

    override fun getUtxoByAddress(address: String): Set<TransactionInput> {
        return redisClient.normal.getUtxo(address)
    }

    override fun getAddressAll(): Set<String> {
        return redisClient.normal.getAddressAll()
    }

    override fun getUtxoAll(): Set<TransactionInput> {
        return redisClient.normal.getUtxoAll()
    }

    private fun addPendingUtxo(transactionId: String, outputIdx: Int, data: TransactionInputOutputBase) {
        redisClient.pending.addUtxo(transactionId, outputIdx, data)
    }

    private fun removePendingUtxo(address: String, transactionId: String, outputIdx: Int) {
        redisClient.pending.removeUtxo(address, transactionId, outputIdx)
    }

    override fun addPendingUtxoFromTransactionInput(transaction: Transaction) {
        transaction.inputs.forEach { input ->
            addPendingUtxo(input.originalTxHash, input.originalOutputIndex, input)
        }
    }

    override fun removePendingUtxoFromTransactionInput(transaction: Transaction) {
        val address = transaction.sourceAddress
        transaction.inputs.forEach { input ->
            removePendingUtxo(address, input.originalTxHash, input.originalOutputIndex)
        }
    }

    override fun hasPendingUtxo(utxo: TransactionInput): Boolean {
        return redisClient.pending.existsUtxo("${utxo.originalTxHash}:${utxo.originalOutputIndex}")
    }

    override fun getPendingUtxoAll(): Set<TransactionInput> {
        return redisClient.pending.getUtxoAll()
    }

    override fun getHeight(): Long {
        return lastBlock!!.height
    }

    override fun cleanCache() {
        redisClient.cleanUp()
        runBlocking {
            client.transaction().deleteMany(Document())
        }
    }
}