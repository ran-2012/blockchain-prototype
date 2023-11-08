package blockchain.network

import blockchain.data.core.Block
import blockchain.network.client.HttpClient
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap

class Broadcaster @JvmOverloads constructor(initialPeerMap: Map<String, String> = HashMap()) {

    private val peerMap: ConcurrentHashMap<String, HttpClient> = ConcurrentHashMap()

    init {
        for (key in initialPeerMap.keys) {
            peerMap[key] = HttpClient(initialPeerMap[key]!!)
        }
    }

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

    private suspend fun <R> aggregateData(func: suspend (httpClient: HttpClient) -> R?): Map<String, R> {
        val map = HashMap<String, R>()
        for (key in getKeySet()) {
            val data = func(peerMap[key]!!)
            if (data != null) {
                map[key] = data
            }
        }
        return map
    }

    suspend fun getBlock(hash: String): Map<String, Block> {
        return aggregateData {
            try {
                it.peerService.getBlockWithHash(hash)
            } catch (ignored: Exception) {
                null
            }
        }
    }

    suspend fun getBlockRange(min: Long, max: Long): Map<String, Map<Long, List<Block>>> {
        return aggregateData {
            try {
                it.peerService.getBlockRange(min, max)
            } catch (ignored: Exception) {
                null
            }
        }
    }
}