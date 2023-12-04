package blockchain.network

import blockchain.data.core.Block
import blockchain.data.core.Transaction
import blockchain.network.INetwork.Callback
import blockchain.network.server.HttpServer
import kotlinx.coroutines.runBlocking

class NetworkInternal @JvmOverloads constructor(serverPort: Int, initialPeerMap: Map<String, String> = HashMap()) :
    INetwork {

    private val server: HttpServer = HttpServer(serverPort)
    private val broadcaster: Broadcaster = Broadcaster(initialPeerMap)

    private var callback = Callback()
    private var globalChainCallback = Callback()

    init {
        runBlocking {
            server.start()
        }
    }

    override fun registerCallback(callback: Callback) {
        this.callback = callback

        server.setCallback(callback)
    }

    override fun registerGlobalChainCallback(callback: Callback) {
        this.globalChainCallback = callback

        server.setGlobalChainCallback(callback)
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

    override fun newTransaction(transaction: Transaction) {
        runBlocking {
            broadcaster.newTransaction(transaction)
        }
    }

    override fun getBlock(hash: String): Map<String, Block> {
        return runBlocking {
            broadcaster.getBlock(hash)
        }
    }

    override fun getUserLocation(address: String): String {
        return runBlocking {
            broadcaster.getUserLocation(address)
        }
    }

    override fun moveUser(
        address: String,
        localChainId: String,
        signatures: MutableList<Transaction.Signature>
    ): List<Transaction.Signature>? {
        return runBlocking {
            broadcaster.moveUser(address, localChainId, signatures)
        }
    }

    override fun globalNewBlock(block: Block) {
        return runBlocking {
            broadcaster.globalNewBlock(block)
        }
    }

    override fun globalNewTransaction(transaction: Transaction) {
        runBlocking {
            broadcaster.globalNewTransaction(transaction)
        }
    }

    override fun globalGetUserLocation(address: String): String {
        return runBlocking {
            broadcaster.globalGetUserLocation(address)
        }
    }

    override fun globalMoveUser(
        address: String,
        localChainId: String,
        signatures: List<Transaction.Signature>
    ) {
        runBlocking {
            broadcaster.globalMoveUser(address, localChainId, signatures)
        }
    }

    override fun getBlockRange(heightMin: Long, heightMax: Long): Map<String, Map<Long, Block>> {
        return runBlocking {
            broadcaster.getBlockRange(heightMin, heightMax)
        }
    }
}