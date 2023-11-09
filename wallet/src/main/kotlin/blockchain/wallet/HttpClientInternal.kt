package blockchain.wallet

import blockchain.data.core.Transaction
import blockchain.data.core.Utxo
import blockchain.network.client.HttpClient
import kotlinx.coroutines.runBlocking

class HttpClientInternal(url: String) {
    private val httpClient = HttpClient(url)

    fun getTransaction(sourceAddress: String, targetAddress: String, value: Long): Transaction {
        return runBlocking {
            httpClient.walletService.getTransaction(sourceAddress, targetAddress, value)!!
        }
    }

    fun postSignedTransaction(transaction: Transaction) {
        runBlocking {
            httpClient.walletService.newTransaction(transaction)
        }
    }

    fun getUtxoList(sourceAddress: String): List<Utxo> {
        return runBlocking {
            httpClient.walletService.getUtxo(sourceAddress)
        }
    }
}