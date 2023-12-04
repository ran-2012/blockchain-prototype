package blockchain.network.server

import blockchain.data.core.Transaction
import blockchain.data.core.TransactionInput
import blockchain.network.INetwork
import blockchain.network.core.WalletService
import blockchain.storage.Storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async

class WalletController(coroutineContext: CoroutineScope, callback: INetwork.Callback) :
    BaseController(coroutineContext, callback),
    WalletService {

    override suspend fun getTransaction(sourceAddress: String, targetAddress: String, value: String): Transaction? {
        val transaction = scope.async {
            try {
                callback.onNewTransactionRequested(sourceAddress, targetAddress, value)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        return transaction.await()
    }

    override suspend fun newTransaction(transaction: Transaction) {
        val _e = scope.async {
            try {
                callback.onSignedTransactionReceived(transaction)
                null
            } catch (e: Exception) {
                e
            }
        }
        val e = _e.await()
        if (e != null) {
            throw e
        }
    }

    override suspend fun getUtxo(address: String): List<TransactionInput> {
        // Processed by storage module directly
        return Storage.getInstance().getUtxoByAddress(address).toList()
    }
}