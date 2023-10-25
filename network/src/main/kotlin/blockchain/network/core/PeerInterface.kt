package blockchain.network.core

import blockchain.data.core.BlockData

interface PeerInterface {
    suspend fun newBlock(blockData: BlockData)

    suspend fun getBlock(min: Long, max: Long): Map<Long, List<BlockData>>
}