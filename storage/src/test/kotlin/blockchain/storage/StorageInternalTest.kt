package blockchain.storage

import blockchain.data.core.Block
import org.junit.jupiter.api.*

class StorageInternalTest {

    companion object {
        private lateinit var storageInternal: StorageInternal

        @JvmStatic
        @BeforeAll
        fun setUp() {
            storageInternal = StorageInternal("test")
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            storageInternal.cleanUp()
        }
    }

    @BeforeEach
    fun cleanUp() {
        storageInternal.cleanUp()
    }

    @Test
    fun addBlockSync() {
        val block = Block(1, "1")
        storageInternal.addBlockSync(block)
        var map = storageInternal.getBlockAllSync()

        Assertions.assertEquals(map.size, 1)
        Assertions.assertEquals(map[1]!!.size, 1)
        Assertions.assertEquals(map[1]!![0].hash, "1")
        Assertions.assertEquals(map[1]!![0].height, 1)

        storageInternal.addBlockSync(Block(2, "2"))
        map = storageInternal.getBlockAllSync()

        Assertions.assertEquals(map.size, 2)
    }

    @Test
    fun removeBlockSync() {
    }

    @Test
    fun removeBlockRangeSync() {
    }

    @Test
    fun getBlockAllSync() {
    }

    @Test
    fun getBlockRangeSync() {
    }

    @Test
    fun getBlockSync() {
    }

    @Test
    fun addTransactionSync() {
    }

    @Test
    fun removeTransactionSync() {
    }

    @Test
    fun getTransactionSync() {
    }

    @Test
    fun addUtxoSync() {
    }

    @Test
    fun removeUtxoSync() {
    }

    @Test
    fun getUtxoListSync() {
    }

    @Test
    fun getAddress() {
    }


}