package blockchain.mining

import blockchain.network.Network
import blockchain.storage.Storage
import blockchain.utility.Log
import com.beust.jcommander.JCommander

fun main(args: Array<String>) {
    val config = Config()
    JCommander.newBuilder()
        .addObject(config)
        .build()
        .parse(*args)

    Log.setTag(config.name)
    val log = Log.get("Mining")

    log.info("Starting Node: {}, at port: {}, enable mining: {}", config.name, config.port, config.isMiner)
    log.info("Peer list: {}", config.peers)

    Thread.setDefaultUncaughtExceptionHandler { t: Thread, e: Throwable ->
        log.error("Unhandled exception in thread: {}", t.name)
        e.printStackTrace()
    }

    Storage.initialize(config.name)

    val peerMap = HashMap<String, String>()
    for (i in config.peers.indices) {
        peerMap[i.toString()] = config.peers[i]
    }
    Network.init(config.port, peerMap)

    val service = BlockService(config.isMiner, config.publicKey)

    Runtime.getRuntime().addShutdownHook(Thread {
        log.warn("Exiting, node: {}", config.name)
        service.stop()
    })

    service.start()

    while (true) {
        try {
            Thread.sleep(Long.MAX_VALUE)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }
}