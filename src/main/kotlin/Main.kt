import blockchain.data.core.Block
import blockchain.data.core.Transaction
import blockchain.data.core.TransactionOutput
import blockchain.storage.StorageInternal
import blockchain.utility.Log
import blockchain.wallet.Config
import java.util.concurrent.TimeUnit

fun generateNodeParameter(index: Int, port: Int, publicKey: String, isMiner: Boolean): List<String> {
    val fromIdea = false
    val ideaParameter =
        "\"-javaagent:C:\\Program Files\\JetBrains\\IntelliJ IDEA 2023.2.3\\lib\\idea_rt.jar=3132:C:\\Program Files\\JetBrains\\IntelliJ IDEA 2023.2.3\\bin\" -Dfile.encoding=UTF-8 -classpath E:\\project\\blockchain-prototype\\mining\\build\\classes\\java\\main;E:\\project\\blockchain-prototype\\mining\\build\\classes\\kotlin\\main;E:\\project\\blockchain-prototype\\mining\\build\\resources\\main;E:\\project\\blockchain-prototype\\storage\\build\\classes\\java\\main;E:\\project\\blockchain-prototype\\storage\\build\\classes\\kotlin\\main;E:\\project\\blockchain-prototype\\network\\build\\classes\\java\\main;E:\\project\\blockchain-prototype\\network\\build\\classes\\kotlin\\main;E:\\project\\blockchain-prototype\\network\\build\\resources\\main;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\com.squareup.retrofit2\\converter-gson\\2.9.0\\fc93484fc67ab52b1e0ccbdaa3922d8a6678e097\\converter-gson-2.9.0.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\com.squareup.retrofit2\\retrofit\\2.9.0\\d8fdfbd5da952141a665a403348b74538efc05ff\\retrofit-2.9.0.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\com.squareup.okhttp3\\okhttp\\4.11.0\\436932d695b2c43f2c86b8111c596179cd133d56\\okhttp-4.11.0.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.jetbrains.kotlinx\\kotlinx-coroutines-core-jvm\\1.7.3\\2b09627576f0989a436a00a4a54b55fa5026fb86\\kotlinx-coroutines-core-jvm-1.7.3.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.jetbrains.kotlin\\kotlin-stdlib\\1.9.21\\17ee3e873d439566c7d8354403b5f3d9744c4c9c\\kotlin-stdlib-1.9.21.jar;E:\\project\\blockchain-prototype\\data\\build\\classes\\java\\main;E:\\project\\blockchain-prototype\\utility\\build\\classes\\java\\main;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\com.beust\\jcommander\\1.82\\a7c5fef184d238065de38f81bbc6ee50cca2e21\\jcommander-1.82.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\io.javalin\\javalin\\5.6.1\\8ba687740a87baf2b786de22c331b86e3b63da35\\javalin-5.6.1.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.slf4j\\slf4j-simple\\2.0.7\\bfa4d4dad645a5b11c022ae0043bac2df6cf16b5\\slf4j-simple-2.0.7.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.jetbrains.kotlin\\kotlin-stdlib-jdk8\\1.8.21\\67f57e154437cd9e6e9cf368394b95814836ff88\\kotlin-stdlib-jdk8-1.8.21.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.jetbrains\\annotations\\24.0.1\\13c5c75c4206580aa4d683bffee658caae6c9f43\\annotations-24.0.1.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.eclipse.jetty.websocket\\websocket-jetty-server\\11.0.15\\f7b97864b77599f594b8d44449acad3f6f60920f\\websocket-jetty-server-11.0.15.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.eclipse.jetty\\jetty-webapp\\11.0.15\\ba945a86dbd910346c32e1eacdb36aa15a5c18dd\\jetty-webapp-11.0.15.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.eclipse.jetty\\jetty-server\\11.0.15\\ce2fc063638c702f2df749dd23cde6c41c7b0c06\\jetty-server-11.0.15.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.slf4j\\slf4j-api\\2.0.7\\41eb7184ea9d556f23e18b5cb99cad1f8581fc00\\slf4j-api-2.0.7.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.eclipse.jetty.websocket\\websocket-jetty-api\\11.0.15\\32eba5745565d8a02c4564abb5c20a1047c7e785\\websocket-jetty-api-11.0.15.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\com.squareup.okio\\okio-jvm\\3.2.0\\332d1c5dc82b0241cb1d35bb0901d28470cc89ca\\okio-jvm-3.2.0.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.jetbrains.kotlin\\kotlin-stdlib-jdk7\\1.8.21\\7473b8cd3c0ef9932345baf569bc398e8a717046\\kotlin-stdlib-jdk7-1.8.21.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.eclipse.jetty\\jetty-annotations\\11.0.15\\f63b1aef0df6076243af8bc4ecd6e64ddc4163c2\\jetty-annotations-11.0.15.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.eclipse.jetty.websocket\\websocket-servlet\\11.0.15\\cd9d5af178d8f78ffd155c9b1355a4ae45ece6b4\\websocket-servlet-11.0.15.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.eclipse.jetty\\jetty-servlet\\11.0.15\\1947cbf5c92f4edf9712a162f4926c838dfd03a\\jetty-servlet-11.0.15.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.eclipse.jetty.websocket\\websocket-jetty-common\\11.0.15\\f03ea072ebf26547c33f15890026e253daf02c93\\websocket-jetty-common-11.0.15.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.eclipse.jetty.toolchain\\jetty-jakarta-servlet-api\\5.0.2\\27fce6d666a203526236d33d00e202a4136230f\\jetty-jakarta-servlet-api-5.0.2.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.eclipse.jetty\\jetty-xml\\11.0.15\\8189a8dfd871415b768d6568476e33a553e80b3\\jetty-xml-11.0.15.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.eclipse.jetty\\jetty-http\\11.0.15\\6eb099ce51496de87ecfe9b8c62c2e8f3f5e848\\jetty-http-11.0.15.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.eclipse.jetty\\jetty-io\\11.0.15\\e334388b4ae2aa4c59a1715f707237e31d663d81\\jetty-io-11.0.15.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.eclipse.jetty\\jetty-plus\\11.0.15\\e9a632c5a041c0e3ac473e902e9a1fbe5af9d558\\jetty-plus-11.0.15.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\jakarta.annotation\\jakarta.annotation-api\\2.1.1\\48b9bda22b091b1f48b13af03fe36db3be6e1ae3\\jakarta.annotation-api-2.1.1.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.ow2.asm\\asm-commons\\9.5\\19ab5b5800a3910d30d3a3e64fdb00fd0cb42de0\\asm-commons-9.5.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.ow2.asm\\asm\\9.5\\dc6ea1875f4d64fbc85e1691c95b96a3d8569c90\\asm-9.5.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.eclipse.jetty.websocket\\websocket-core-server\\11.0.15\\9de6c8b8c2c0c8760d0c2934462a32b0887f2b6f\\websocket-core-server-11.0.15.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.eclipse.jetty\\jetty-security\\11.0.15\\b59d5d57f476502b5382a53ef208e2c382c3e2d2\\jetty-security-11.0.15.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.eclipse.jetty.websocket\\websocket-core-common\\11.0.15\\84dc1b8cc2f36f7c7113dd9247d17ab3f842c132\\websocket-core-common-11.0.15.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.eclipse.jetty\\jetty-util\\11.0.15\\de81765b3da6dc68ddf5acc87dfe9a63408c64fb\\jetty-util-11.0.15.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.eclipse.jetty\\jetty-jndi\\11.0.15\\b30ae05ab1e2b9db05ff09dffd1e71ed3b6d7769\\jetty-jndi-11.0.15.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\jakarta.transaction\\jakarta.transaction-api\\2.0.0\\24a0525b4acfbca4086d2f1278be3a084fe1c67d\\jakarta.transaction-api-2.0.0.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.ow2.asm\\asm-tree\\9.5\\fd33c8b6373abaa675be407082fdfda35021254a\\asm-tree-9.5.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.mongodb\\mongodb-driver-kotlin-coroutine\\4.10.1\\1045c02aa80d80f52b8b93c05dc68d6fb6c5b736\\mongodb-driver-kotlin-coroutine-4.10.1.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\redis.clients\\jedis\\5.0.2\\47d917ce322cef3fc1fbe7534f351e25d977e52b\\jedis-5.0.2.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\com.google.code.gson\\gson\\2.10.1\\b3add478d4382b78ea20b1671390a858002feb6c\\gson-2.10.1.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.apache.logging.log4j\\log4j-core\\2.21.0\\122e1a9e0603cc9eae07b0846a6ff01f2454bc49\\log4j-core-2.21.0.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.apache.logging.log4j\\log4j-api\\2.21.0\\760192f2b69eacf4a4afc78e5a1d7a8de054fcbd\\log4j-api-2.21.0.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\commons-codec\\commons-codec\\1.16.0\\4e3eb3d79888d76b54e28b350915b5dc3919c9de\\commons-codec-1.16.0.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.jetbrains.kotlinx\\kotlinx-coroutines-reactive\\1.7.3\\3f30c8dd5cd0993ca2804591210141be762786f8\\kotlinx-coroutines-reactive-1.7.3.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.mongodb\\bson-kotlin\\4.10.1\\69e110005d1849067edea7b8f1ea644cb9b7c5b1\\bson-kotlin-4.10.1.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.mongodb\\mongodb-driver-reactivestreams\\4.10.1\\275e4d547f2183276a34605f92a16ff61dae8f71\\mongodb-driver-reactivestreams-4.10.1.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.mongodb\\bson\\4.10.1\\64a0f717d52e2834bcd4653888720f96b2691e9b\\bson-4.10.1.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.apache.commons\\commons-pool2\\2.11.1\\8970fd110c965f285ed4c6e40be7630c62db6f68\\commons-pool2-2.11.1.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.json\\json\\20231013\\e22e0c040fe16f04ffdb85d851d77b07fc05ea52\\json-20231013.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.reactivestreams\\reactive-streams\\1.0.4\\3864a1320d97d7b045f729a326e1e077661f31b7\\reactive-streams-1.0.4.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.jetbrains.kotlin\\kotlin-reflect\\1.8.10\\1e90b778ea4669b6bcbfaca57313665ddd804779\\kotlin-reflect-1.8.10.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.mongodb\\mongodb-driver-core\\4.10.1\\3f2d62186a192733b145bdf2078516b3847f9138\\mongodb-driver-core-4.10.1.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\io.projectreactor\\reactor-core\\3.5.0\\83749d14f5795905e5b8e8c258c917d4707b586c\\reactor-core-3.5.0.jar;C:\\Users\\ran\\.gradle\\caches\\modules-2\\files-2.1\\org.mongodb\\bson-record-codec\\4.10.1\\65944cf5d0103e7fc82ab3a3d2a9f125926dbe48\\bson-record-codec-4.10.1.jar blockchain.mining.MainKt"
    val list = ArrayList<String>()

    if (fromIdea) {
        list.add("java")
        list.addAll(ideaParameter.split(" "))
    } else {
        list.addAll(
            arrayOf(
                "java", "-Dfile.encoding=UTF-8", "-jar",
                "mining.jar",
            ).asList()
        )
    }

    list.addAll(
        arrayOf(
            "--name", index.toString(), "--port", port.toString(),
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

    val nodeCount = try {
        args[0].toInt()
    } catch (ignored: Exception) {
        5
    }
    val minerCount = try {
        args[1].toInt()
    } catch (ignored: Exception) {
        2
    }

    log.info("Node count: {}, miner count: {}", nodeCount, minerCount)

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

    Runtime.getRuntime().addShutdownHook(Thread {
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
        log.warn("Shutting down")
    })

    while (true) {
        Thread.sleep(Long.MAX_VALUE)
    }
}