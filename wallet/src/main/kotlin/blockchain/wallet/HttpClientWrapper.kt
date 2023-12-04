package blockchain.wallet

import blockchain.data.core.Transaction
import blockchain.data.core.TransactionInput
import blockchain.network.client.HttpClient
import kotlinx.coroutines.runBlocking

class HttpClientWrapper(url: String) {
    private val httpClient = HttpClient(url)

    fun getTransaction(sourceAddress: String, targetAddress: String, data: String): Transaction {
        return runBlocking {
            httpClient.walletService.getTransaction(sourceAddress, targetAddress, data)!!
        }
    }

    fun postSignedTransaction(transaction: Transaction) {
        runBlocking {
            httpClient.walletService.newTransaction(transaction)
        }
    }

    fun getUtxoList(sourceAddress: String): List<TransactionInput> {
        return runBlocking {
            httpClient.walletService.getUtxo(sourceAddress)
        }
    }
}