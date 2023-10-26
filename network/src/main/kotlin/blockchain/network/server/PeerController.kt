package blockchain.network.server

import blockchain.data.core.BlockData
import blockchain.network.core.PeerService

class PeerController : PeerService {
    override suspend fun newBlock(blockData: BlockData) {
        TODO("Not yet implemented")
    }

    override suspend fun getBlockWithId(id: Long): List<BlockData> {
        TODO("Not yet implemented")
    }

    override suspend fun getBlockRange(min: Long, max: Long): Map<Long, List<BlockData>> {
        TODO("Not yet implemented")
    }

    override suspend fun heartbeat() {
        TODO("Not yet implemented")
    }

}
