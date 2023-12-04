package blockchain.network

import blockchain.data.core.Block
import blockchain.data.core.Transaction
import blockchain.data.core.Transaction.Signature
import blockchain.network.client.HttpClient
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap

class Broadcaster @JvmOverloads constructor(
    localPeerMap: Map<String, String> = HashMap(),
    globalPeerMap: Map<String, String> = HashMap()
) {

    private val peerMap: ConcurrentHashMap<String, HttpClient> = ConcurrentHashMap()
    private val globalPeerMap: HashMap<String, HttpClient> = HashMap()

    init {
        for (key in localPeerMap.keys) {
            peerMap[key] = HttpClient(localPeerMap[key]!!)
        }
        for (key in globalPeerMap.keys) {
            this.globalPeerMap[key] = HttpClient(globalPeerMap[key]!!)
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

    suspend fun newTransaction(transaction: Transaction) {
        for (key in getKeySet()) {
            peerMap[key]?.peerService?.newTransaction(transaction)
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

    suspend fun getBlockRange(min: Long, max: Long): Map<String, Map<Long, Block>> {
        return aggregateData {
            try {
                it.peerService.getBlockRange(min, max)
            } catch (ignored: Exception) {
                null
            }
        }
    }

    suspend fun getUserLocation(address: String): String {
        for (client in globalPeerMap.values) {
            val url = client.peerService.getUserLocation(address)
            if (url.isNotEmpty()) {
                return url
            }
        }
        return ""
    }

    suspend fun moveUser(
        address: String,
        localChainId: String,
        signatures: List<Signature>
    ): List<Signature> {
        val list = ArrayList<Signature>()
        for (client in globalPeerMap.values) {
            val list2 = client.peerService.moveUser(address, localChainId, signatures)
            if (list.isNotEmpty()) {
                list.addAll(list2)
                break
            }
        }
        return list
    }

    suspend fun globalNewBlock(block: Block) {
        for (client in globalPeerMap.values) {
            client.peerService.globalNewBlock(block)
        }
    }

    suspend fun globalNewTransaction(transaction: Transaction) {
        for (client in globalPeerMap.values) {
            client.peerService.globalNewTransaction(transaction)
        }
    }


    suspend fun globalGetUserLocation(address: String): String {
        for (client in globalPeerMap.values) {
            val url = client.peerService.globalGetUserLocation(address)
            if (url.isNotEmpty()) {
                return url
            }
        }
        return ""
    }

    suspend fun globalMoveUser(address: String, localChainId: String, signatures: List<Transaction.Signature>): ArrayList<Signature> {
        val list = ArrayList<Signature>()
        for (client in globalPeerMap.values) {
            val list2 = client.peerService.globalMoveUser(address, localChainId, signatures)
            if (list.isNotEmpty()) {
                list.addAll(list2)
                break
            }
        }
        return list
    }
}