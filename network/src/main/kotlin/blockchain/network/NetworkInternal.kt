package blockchain.network

import blockchain.data.core.Block
import blockchain.network.INetwork.Callback
import blockchain.network.server.HttpServer
import kotlinx.coroutines.runBlocking

class NetworkInternal @JvmOverloads constructor(serverPort: Int, initialPeerMap: Map<String, String> = HashMap()) : INetwork {

    private val server: HttpServer = HttpServer(serverPort)
    private val broadcaster: Broadcaster = Broadcaster(initialPeerMap)

    private var callback = Callback()

    override fun registerCallback(callback: Callback) {
        this.callback = callback

        server.setCallback(callback)
    }

    override fun unregisterCallback(callback: Callback) {
        this.callback = callback

        server.setCallback(Callback())
    }

    override fun newBlock(block: Block) {
        runBlocking {
            broadcaster.newBlock(block)
        }
    }

    override fun getBlock(hash: String) {
        runBlocking {
            broadcaster.getBlock(hash)
        }
    }

    override fun getBlockRange(heightMin: Long, heightMax: Long) {
        runBlocking {
            broadcaster.getBlockRange(heightMin, heightMax)
        }
    }
}