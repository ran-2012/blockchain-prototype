import blockchain.network.server.HttpServer
import blockchain.utility.Log
import kotlinx.coroutines.*
import java.lang.RuntimeException
import kotlin.Exception

fun main(args: Array<String>) {
    val server = HttpServer(7070)

    val log = Log.get("Main")
    log.info("Start")
    val scope = CoroutineScope(Dispatchers.IO +
            CoroutineExceptionHandler { _, e ->
                e.printStackTrace()
                log.error("Something wrong here")
            })
    try {
        runBlocking {
            throw RuntimeException("1")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    try {
        runBlocking {
            throw RuntimeException("2")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    log.info("end")
}