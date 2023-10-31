package blockchain.storage

import blockchain.data.core.Transaction
import kotlinx.coroutines.runBlocking

class StorageInternal(dbName: String) : IStorage {

    private val client: DataBaseClient;

    init {
        client = DataBaseClient(dbName)
        runBlocking {
            client.initCollection()
            client.initSchema()
        }
    }

    override fun addBlockSync(data: Block?) {
        runBlocking {

        }
        TODO("Not yet implemented")
    }

    override fun removeBlockSync(height: String?) {
        TODO("Not yet implemented")
    }

    override fun removeBlockRangeSync(heightMin: Long?, heightMax: Long?) {
        TODO("Not yet implemented")
    }

    override fun getBlockAllSync(): MutableMap<Long, MutableList<Block>> {
        TODO("Not yet implemented")
    }

    override fun getBlockRangeSync(heightMin: Long?, heightMax: Long?): MutableMap<Long, MutableList<Block>> {
        TODO("Not yet implemented")
    }

    override fun getBlockSync(): MutableList<Block> {
        TODO("Not yet implemented")
    }

    override fun addTransactionSync(data: Transaction?) {
        TODO("Not yet implemented")
    }

    override fun removeTransactionSync(transactionId: String?) {
        TODO("Not yet implemented")
    }

    override fun getTransactionSync(pubKey: String?): MutableList<Transaction> {
        TODO("Not yet implemented")
    }

}