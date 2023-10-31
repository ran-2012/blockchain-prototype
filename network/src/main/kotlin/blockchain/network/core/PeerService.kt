package blockchain.network.core

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PeerService {
    @POST(BLOCKS)
    suspend fun newBlock(Block: Block)

    @GET(GET_BLOCK_WITH_ID)
    suspend fun getBlockWithId(id: Long): List<Block>

    @GET(BLOCKS)
    suspend fun getBlockRange(@Query("min") min: Long, @Query("max") max: Long): Map<Long, List<Block>>

    @GET(HEARTBEAT)
    suspend fun heartbeat()

    companion object {
        const val BLOCKS = "blocks"
        const val GET_BLOCK_WITH_ID = "block/{id}"
        const val HEARTBEAT = "heartbeat"
    }
}