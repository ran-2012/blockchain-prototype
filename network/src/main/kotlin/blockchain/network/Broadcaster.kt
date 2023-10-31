package blockchain.network

import blockchain.network.client.HttpClient
import blockchain.storage.Block
import java.util.concurrent.ConcurrentHashMap

class Broadcaster {

    private val peerMap: ConcurrentHashMap<String, HttpClient> = ConcurrentHashMap()

    fun addPeer(id: String, url: String) {
        peerMap[id] = HttpClient(url)
    }

    fun removePeer(id: String) {
        peerMap.remove(id)
    }

    private fun getKeySet(): HashSet<String> {
        return HashSet(peerMap.keys)
    }

    suspend fun newBlock(block: Block) {
        for (key in getKeySet()) {
            peerMap[key]?.peerService?.newBlock(block)
        }
    }

    private suspend fun <R> aggregateData(func: suspend (httpClient: HttpClient?) -> R?): Map<String, R> {
        val map = HashMap<String, R>()
        for (key in getKeySet()) {
            val data = func(peerMap[key])
            if (data != null) {
                map[key] = data
            }
        }
        return map
    }

    suspend fun getBlockById(blockId: Long): Map<String, List<Block>> {
        return aggregateData {
            it?.peerService?.getBlockWithId(blockId)
        }
    }

    suspend fun getBlockRange(min: Long, max: Long): Map<String, Map<Long, List<Block>>> {
        return aggregateData {
            it?.peerService?.getBlockRange(min, max)
        }
    }
}