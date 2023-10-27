package blockchain.storage

import blockchain.data.core.BlockData
import blockchain.data.core.TransactionData

class StorageInternal: IStorage{
    override fun addBlockSync(data: BlockData?) {
        TODO("Not yet implemented")
    }

    override fun removeBlockSync(blockId: String?) {
        TODO("Not yet implemented")
    }

    override fun removeBlockRangeSync(min: Long?, max: Long?) {
        TODO("Not yet implemented")
    }

    override fun getBlockAllSync(): MutableMap<Long, MutableList<BlockData>> {
        TODO("Not yet implemented")
    }

    override fun getBlockRangeSync(min: Long?, max: Long?): MutableMap<Long, MutableList<BlockData>> {
        TODO("Not yet implemented")
    }

    override fun getBlockSync(): MutableList<BlockData> {
        TODO("Not yet implemented")
    }

    override fun addTransactionSync(data: TransactionData?) {
        TODO("Not yet implemented")
    }

    override fun removeTransactionSync(transactionId: String?) {
        TODO("Not yet implemented")
    }

    override fun getTransactionSync(pubKey: String?): MutableList<TransactionData> {
        TODO("Not yet implemented")
    }

}