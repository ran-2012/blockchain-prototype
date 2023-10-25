package blockchain.network.server

import blockchain.data.wallet.TransactionRequestData
import jakarta.transaction.Transaction

class WalletController {

    suspend fun newTransaction(transaction: TransactionRequestData) {
        TODO("Not yet implemented")
    }

    suspend fun getBalance(publicKey: String) {
        TODO("Not yet implemented")
    }
}