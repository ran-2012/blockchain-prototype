package blockchaing.network

import blockchain.data.core.Block
import blockchain.network.INetwork
import blockchain.network.INetwork.Callback
import blockchain.network.client.HttpClient
import blockchain.network.server.HttpServer
import blockchain.storage.Storage
import blockchain.storage.StorageInternal
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HttpServerTest {

    companion object {
        init {
            Storage.initialize("test")
        }

        val storage = (Storage.getInstance()) as StorageInternal
        const val port = 7070
        val server = HttpServer(port)
        val client = HttpClient("http://localhost:$port")

        val callback = object : Callback() {
        }

        @JvmStatic
        @BeforeAll
        fun setup(): Unit {
            storage.cleanUp()
            runBlocking {
                server.start()
                server.setCallback(callback)
            }
        }

        @JvmStatic
        @AfterAll
        fun tearDown(): Unit {
            storage.cleanUp()
            server.stop()
        }
    }

    @BeforeEach
    fun cleanUp() {
        storage.cleanUp()
    }

    @Test
    fun addNewBlock() {
    }
}