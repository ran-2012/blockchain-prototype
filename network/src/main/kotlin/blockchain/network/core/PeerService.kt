package blockchain.network.core

import blockchain.data.core.Block
import blockchain.data.core.Transaction
import blockchain.data.core.Transaction.Signature
import retrofit2.http.*

interface PeerService {
    @POST(BLOCKS)
    suspend fun newBlock(@Body block: Block)

    @POST(TRANSACTION)
    suspend fun newTransaction(@Body transaction: Transaction)

    @GET(BLOCKS_WITH_HASH)
    suspend fun getBlockWithHash(@Path("hash") hash: String): Block?

    @GET(BLOCKS)
    suspend fun getBlockRange(@Query(PARAM_MIN) min: Long, @Query(PARAM_MAX) max: Long): Map<Long, Block>

    @GET("user-location")
    suspend fun getUserLocation(@Query("address") address: String): String

    @POST("move-user")
    suspend fun moveUser(
        @Query("address") address: String,
        @Query("local-chain-id") localChainId: String,
        signatures: List<Signature>
    ): List<Signature>

    @GET(HEARTBEAT)
    suspend fun heartbeat()

    @POST("global/$BLOCKS")
    suspend fun globalNewBlock(@Body block: Block)

    @POST("global/$TRANSACTION")
    suspend fun globalNewTransaction(@Body transaction: Transaction)

    @GET("global/user-location")
    suspend fun globalGetUserLocation(@Query("address") address: String): String

    @POST("global/move-user")
    suspend fun globalMoveUser(
        @Query("address") address: String,
        @Query("local-chain-id") localChainId: String,
        signatures: List<Signature>
    ): List<Signature>

    companion object {
        const val BLOCKS = "blocks"
        const val BLOCKS_WITH_HASH = "blocks/{hash}"
        const val TRANSACTION = "transaction"
        const val HEARTBEAT = "heartbeat"

        const val PARAM_MIN = "min"
        const val PARAM_MAX = "max"
    }
}