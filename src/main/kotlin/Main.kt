import blockchain.network.server.HttpServer
import blockchain.utility.Log

val log = Log.get("Main")

fun main(args: Array<String>) {
    log.info("this is a message")

    log.info("template message {}+{}", 1, 1)
    val server = HttpServer()
    server.start()

}