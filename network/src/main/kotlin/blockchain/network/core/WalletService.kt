package blockchain.network.core

import blockchain.data.core.Transaction
import blockchain.data.core.TransactionInput
import blockchain.data.core.TransactionOutput
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface WalletService {

    @GET(Companion.TRANSACTION)
    suspend fun getTransaction(
        @Query(PARAM_SOURCE_ADDRESS) sourceAddress: String,
        @Query(PARAM_TARGET_ADDRESS) targetAddress: String,
        @Query(PARAM_VALUE) value: Long
    ): Transaction?

    @POST(Companion.TRANSACTION)
    suspend fun newTransaction(@Body transaction: Transaction)

    @GET(Companion.UTXO)
    suspend fun getUtxo(@Query(PARAM_ADDRESS) address: String): List<TransactionInput>

    companion object {
        const val TRANSACTION = "transaction"
        const val UTXO = "utxo"

        const val PARAM_ADDRESS = "address"
        const val PARAM_SOURCE_ADDRESS = "source_address"
        const val PARAM_TARGET_ADDRESS = "target_address"
        const val PARAM_VALUE = "value"
    }
}