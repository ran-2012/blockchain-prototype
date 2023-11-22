package blockchain.network.core

import blockchain.data.core.Block
import blockchain.data.core.Transaction
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

    @GET(HEARTBEAT)
    suspend fun heartbeat()

    companion object {
        const val BLOCKS = "blocks"
        const val BLOCKS_WITH_HASH = "blocks/{hash}"
        const val TRANSACTION = "transaction"
        const val HEARTBEAT = "heartbeat"

        const val PARAM_MIN = "min"
        const val PARAM_MAX = "max"
    }
}