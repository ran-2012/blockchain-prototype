package blockchain.mining

import blockchain.data.core.Transaction
import blockchain.data.core.Transaction.Signature
import blockchain.data.core.Transaction.SignatureType
import blockchain.mining.BaseBlockService.Callback
import blockchain.network.Network
import blockchain.storage.Storage
import blockchain.utility.Hash
import blockchain.utility.Log
import blockchain.utility.Rsa

fun main(args: Array<String>) {
    val config = Config()

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


    val localService = LocalBlockService(config.isMiner, config.publicKey)
    val globalService =
        if (config.isInGlobalChain) {
            GlobalBlockService(config.isMiner, config.publicKey, config.privateKey)
        } else {
            null
        }

    val localStorage = Storage.getInstance()
    val globalStorage = Storage.getGlobalInstance()
    val network = Network.getInstance()

    localService.setCallback(object : Callback() {
        override fun onGetUserDataLocation(address: String): String {
            return if (localStorage.hasAddress(address)) {
                config.url
            } else {
                if (config.isInGlobalChain) {
                    network.globalGetUserLocation(address)
                } else {
                    ""
                }
            }
        }

        override fun onMoveUser(
            address: String,
            localChainId: String,
            signatures: MutableList<Signature>
        ): MutableList<Signature> {
            val list = ArrayList<Signature>()

            if (config.isInGlobalChain) {
                network.globalMoveUser(address, localChainId, signatures)
            }
            return list
        }
    })

    globalService?.setCallback(object : Callback() {
        override fun onGetUserDataLocation(address: String): String {
            return if (localStorage.hasAddress(address)) {
                config.url
            } else {
                ""
            }
        }

        override fun onMoveUser(
            address: String,
            localChainId: String,
            signatures: MutableList<Signature>
        ): List<Signature> {
            val list = ArrayList<Signature>()
            if (localStorage.hasAddress(address)) {
                var signed = false
                for (sig in signatures) {
                    if (sig.type == SignatureType.USER &&
                        Hash.hashString(sig.publicKey) == address &&
                        Rsa.verify(sig.data, sig.signature, sig.publicKey)
                    ) {
                        signed = true
                    }
                }
                if (!signed) {
                    return list
                }
                val nullAddress = "0".repeat(64)
                val transaction = localService.generateNewTransaction(address, nullAddress, "")
                transaction.signatures.addAll(signatures)
                network.newTransaction(transaction)

                val transactionGlobal = globalService.generateNewTransaction(address, address, "", localChainId)
                transactionGlobal.signatures.addAll(signatures)
                network.globalNewTransaction(transactionGlobal)
            }
            return list
        }
    })

    Runtime.getRuntime().addShutdownHook(Thread {
        log.warn("Exiting, node: {}", config.name)
        localService.stop()
        globalService?.stop()
    })

    localService.start()
    globalService?.stop()

    while (true) {
        try {
            Thread.sleep(Long.MAX_VALUE)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }
}