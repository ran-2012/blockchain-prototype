package blockchain.network.server

import blockchain.data.core.Block
import blockchain.network.core.PeerService

class PeerController : PeerService {
    override suspend fun newBlock(Block: Block) {
        TODO("Not yet implemented")
    }

    override suspend fun getBlockWithId(id: Long): List<Block> {
        TODO("Not yet implemented")
    }

    override suspend fun getBlockRange(min: Long, max: Long): Map<Long, List<Block>> {
        TODO("Not yet implemented")
    }

    override suspend fun heartbeat() {
        TODO("Not yet implemented")
    }

}
