import blockchain.data.core.Block
import blockchain.data.core.Transaction
import blockchain.data.core.TransactionOutput
import blockchain.storage.StorageInternal
import blockchain.utility.Log
import blockchain.wallet.Config
import java.util.concurrent.TimeUnit


fun generateNodeParameter(index: Int, port: Int, publicKey: String, isMiner: Boolean): List<String> {
    val list = ArrayList(
        arrayOf(
            "java", "-Dfile.encoding=UTF-8", "-jar",
            "mining.jar", "--name", index.toString(), "--port", port.toString(),
            "--public-key", publicKey
        ).asList()
    )
    if (isMiner) {
        list.add("--mine")
    }
    return list
}

fun generatePeerParameter(start: Int, count: Int, except: Int, portBase: Int): List<String> {
    val list = ArrayList<String>()
    for (i in start..<start + count) {
        if (i == except) {
            continue
        }
        list.add("--peer")
        list.add("http://localhost:${portBase + i}")
    }
    return list
}

const val INIT_VALUE: Long = 1_000_000
fun generateBlock0(accountList: List<Config.Pair>): Block {
    val outputList = ArrayList<TransactionOutput>()
    accountList.forEach { pair ->
        outputList.add(TransactionOutput(pair.address, INIT_VALUE))
    }
    val block = Block()
    val transaction = Transaction("", "", ArrayList(), outputList)
    transaction.coinbase = true
    transaction.fee = 0
    transaction.updateHash()

    block.height = 0
    block.difficulty = 0
    block.addTransaction(transaction)
    block.updateBlockHash()

    return block
}

fun main(args: Array<String>) {
    Log.setTag("main")

    val log = Log.get("Main")

    log.info("Working Directory: {} ", System.getProperty("user.dir"))

    val portBase = 7070
    val nodeCount = 2
    val minerCount = 1
    val nodes: MutableList<Process> = ArrayList()

    val config = Config.load()

    while (config.list.size < nodeCount) {
        config.list.add(Config.generateNewPair())
    }

    Config.save(config)

    val block = generateBlock0(config.list)

    for (i in 0..<nodeCount) {
        val storage = StorageInternal(i.toString())
        if (storage.blockAll.isEmpty()) {
            storage.addBlock(block)
        }

        val command = ArrayList(
            generateNodeParameter(
                i, i + portBase,
                config.list[i].pk, i < minerCount
            )
        )
        command.addAll(generatePeerParameter(0, nodeCount, i, portBase))
        val process = ProcessBuilder()
            .command(command)
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
            if (node.isAlive) {
                log.info("Stopping node $i")
                node.destroy()
            }
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