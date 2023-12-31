package blockchain.network.server

import blockchain.data.core.Block
import blockchain.data.core.Transaction
import blockchain.network.INetwork.Callback
import blockchain.network.core.PeerService
import blockchain.network.core.WalletService
import blockchain.utility.Log
import io.javalin.Javalin
import io.javalin.json.JavalinGson
import kotlinx.coroutines.*
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.util.thread.QueuedThreadPool
import java.util.concurrent.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class HttpServer @JvmOverloads constructor(
    private val port: Int,
    private val threadLimit: Int = 10,
    private var callback: Callback = Callback()
) {

    private val log: Log = Log.get(this)

    private val exceptionHandler: CoroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        exception.printStackTrace()
    }

    /**
     * Run callback on single thread to ensure thread safety.
     */
    private val coroutineContext: CoroutineContext =
        Executors.newSingleThreadExecutor().asCoroutineDispatcher() + exceptionHandler
    private val scope: CoroutineScope = CoroutineScope(coroutineContext)

    private var app: Javalin? = null
    private val peerController: PeerController = PeerController(scope, callback)
    private val walletController: WalletController = WalletController(scope, callback)

    fun setCallback(callback: Callback) {
        this.callback = callback

        peerController.callback = callback
        walletController.callback = callback
    }

    /**
     * Suspend till server is started.
     */
    suspend fun start() = suspendCoroutine {
        log.info("Starting server at port $port")
        app = Javalin
            //===========================================//
            // Configuration
            .create { config ->
                config.requestLogger.http { ctx, _ ->
                    log.debug("Request received, method: {}, url: {}", ctx.method(), ctx.url())
                }
                config.jsonMapper(JavalinGson())
                config.jetty.server {
                    Server(QueuedThreadPool(threadLimit))
                }
            }
            .events { event ->
                event.serverStarted {
                    it.resume(Unit)
                }
                event.serverStartFailed {
                    it.resumeWithException(RuntimeException("Failed to start server"))
                }
            }
            //===========================================//
            // Peer interfaces
            .post(PeerService.BLOCKS) { ctx ->
                runBlocking {
                    peerController.newBlock(ctx.bodyAsClass(Block::class.java))
                }
            }
            .get(PeerService.BLOCKS_WITH_HASH) { ctx ->
                runBlocking {
                    val block = peerController.getBlockWithHash(ctx.pathParam("hash"))
                    if (block == null) {
                        ctx.status(404)
                    } else {
                        ctx.json(block)
                    }
                }
            }
            .post(PeerService.TRANSACTION) { ctx ->
                runBlocking {
                    peerController.newTransaction(ctx.bodyAsClass(Transaction::class.java))
                }
            }
            //===========================================//
            // Wallet interfaces
            .get(WalletService.TRANSACTION) { ctx ->
                val sourceAddress = ctx.queryParam(WalletService.PARAM_SOURCE_ADDRESS)!!
                val targetAddress = ctx.queryParam(WalletService.PARAM_TARGET_ADDRESS)!!
                val value = ctx.queryParam(WalletService.PARAM_VALUE)!!.toLong()
                runBlocking {
                    val transaction = walletController.getTransaction(sourceAddress, targetAddress, value)
                    if (transaction == null) {
                        log.warn(
                            "Failed to generate transaction for address {}, to {}, value {}",
                            sourceAddress,
                            targetAddress,
                            value
                        )
                        ctx.status(400)
                    } else {
                        ctx.json(transaction)
                    }
                }
            }
            .get(WalletService.UTXO) { ctx ->
                val address = ctx.queryParam(WalletService.PARAM_ADDRESS)!!
                ctx.json(runBlocking { walletController.getUtxo(address) })
            }
            //===========================================//
            // Miscellaneous interfaces
            .get(PeerService.HEARTBEAT) { ctx ->
                ctx.status(200)
            }
            .get("/exception") {
                throw RuntimeException("test exception")
            }
            //===========================================//
            .start(port)

    }

    fun stop() {
        app?.stop()
    }

}