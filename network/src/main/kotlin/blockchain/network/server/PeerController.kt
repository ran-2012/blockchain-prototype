package blockchain.network.server

import blockchain.data.core.Block
import blockchain.data.core.Transaction
import blockchain.network.INetwork
import blockchain.network.core.PeerService
import blockchain.storage.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.http.Query
import java.lang.Exception

class PeerController(coroutineContext: CoroutineScope, callback: INetwork.Callback) :
    BaseController(coroutineContext, callback), PeerService {
    override suspend fun newBlock(block: Block) {
        scope.launch {
            try {
                callback.onNewBlockReceived(block)
            } catch (e: Exception) {
                log.warn("Failed to process new block")
                e.printStackTrace()
            }
        }
    }

    override suspend fun newTransaction(transaction: Transaction) {
        scope.launch {
            try {
                callback.onSignedTransactionReceived(transaction)
            } catch (e: Exception) {
                log.warn("Failed to process new transaction")
                e.printStackTrace()
            }
        }
    }

    override suspend fun getBlockWithHash(hash: String): Block? {
        val block = scope.async {
            val block = try {
                Storage.getInstance().getBlock(hash)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
            if (block == null) {
                log.warn("Block, hash: {} not found", hash)
            }
            block
        }
        return block.await()
    }

    override suspend fun getBlockRange(min: Long, max: Long): Map<Long, Block> {
        val result = scope.async {
            try {
                Storage.getInstance().getBlockRange(min, max)
            } catch (e: Exception) {
                e.printStackTrace()
                HashMap()
            }
        }
        return result.await()
    }


    override suspend fun heartbeat() {
    }

    override suspend fun globalNewBlock(block: Block) {
        callback.onNewBlockReceived(block)
    }

    override suspend fun globalNewTransaction(transaction: Transaction) {
        callback.onGlobalSignedTransactionReceived(transaction)
    }

    override suspend fun getUserLocation(address: String): String {
        return callback.onGlobalGetUserLocation(address)
    }

    override suspend fun globalMoveUser(
        address: String,
        localChainId: String,
        signatures: List<Transaction.Signature>
    ) {
        callback.onGlobalMoveUser(address, localChainId, signatures)
    }

}
