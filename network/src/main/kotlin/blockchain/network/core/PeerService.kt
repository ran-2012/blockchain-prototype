package blockchain.network.core

import blockchain.data.core.Block
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PeerService {
    @POST(BLOCKS)
    suspend fun newBlock(block: Block)

    @GET(BLOCKS_WITH_HASH)
    suspend fun getBlockWithHash(hash: String): Block?

    @GET(BLOCKS)
    suspend fun getBlockRange(@Query(PARAM_MIN) min: Long, @Query(PARAM_MAX) max: Long): Map<Long, List<Block>>

    @GET(HEARTBEAT)
    suspend fun heartbeat()

    companion object {
        const val BLOCKS = "blocks"
        const val BLOCKS_WITH_HASH = "blocks/{hash}"
        const val HEARTBEAT = "heartbeat"

        const val PARAM_MIN = "min"
        const val PARAM_MAX = "max"
    }
}