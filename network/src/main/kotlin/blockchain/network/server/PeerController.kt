package blockchain.network.server

import blockchain.data.core.BlockData
import blockchain.network.core.PeerInterface

class PeerController : PeerInterface {
    override suspend fun newBlock(blockData: BlockData) {
        TODO("Not yet implemented")
    }

    override suspend fun getBlock(min: Long, max: Long): Map<Long, List<BlockData>> {
        TODO("Not yet implemented")
    }

}
