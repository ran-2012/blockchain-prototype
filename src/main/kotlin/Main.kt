import blockchain.network.server.HttpServer
import blockchain.storage.DataBaseClient
import blockchain.utility.Log
import blockchain.wallet.Cli
import blockchain.wallet.Config
import java.io.BufferedInputStream
import java.util.concurrent.TimeUnit

val log = Log.get("Main")

fun generateNodeParameter(index: Int, port: Int, publicKey: String, isMiner: Boolean): Array<String> {
    return arrayOf(
        "javac", "--jar",
        "node.jar", "--name", index.toString(), "--port", port.toString(),
        "--public-key", publicKey, "--is-miner", isMiner.toString()
    )
}

fun main(args: Array<String>) {
    val portBase = 7070
    val nodeCount = 5
    val minerCount = 1
    val nodes: MutableList<Process> = ArrayList()

    val config = Config.load()

    while (config.list.size < nodeCount) {
        config.list.add(Config.generateNewPair())
    }

    for (i in 0..<nodeCount) {
        val process = ProcessBuilder()
            .command(
                generateNodeParameter(
                    i, i + portBase,
                    config.list[i].pk, i < minerCount
                ).asList()
            )
            .redirectOutput(ProcessBuilder.Redirect.INHERIT)
            .redirectError(ProcessBuilder.Redirect.INHERIT)
            .start()

        nodes.add(process)
    }

    Runtime.getRuntime().exec("javac")
    Runtime.getRuntime().addShutdownHook(Thread {
        log.warn("Shutting down")
        log.warn("Stopping all nodes")

        nodes.forEachIndexed { i, node ->
            log.info("Stopping node $i")
            node.destroy()
        }
        nodes.forEachIndexed { i, node ->
            if (!node.waitFor(5, TimeUnit.SECONDS)) {
                log.warn("Force killing node $i")
                node.destroyForcibly()
            }
        }
    })

    while (true) {
        Thread.sleep(Long.MAX_VALUE)
    }
}