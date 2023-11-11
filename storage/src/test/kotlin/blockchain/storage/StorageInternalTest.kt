package blockchain.storage

import blockchain.data.core.Block
import blockchain.data.core.Transaction
import blockchain.data.core.Utxo
import org.junit.jupiter.api.*
import java.util.Arrays.asList
import java.util.Date

@Suppress("UsePropertyAccessSyntax")
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

    private fun createBlock(height: Long, hash: String): Block {
        val block = Block()
        block.height = height;
        block.hash = hash;
        return block
    }

    private fun createUtxo(address: String, signature: String): Utxo {
        return Utxo(address, 0, signature, false)
    }


    @Test
    fun addBlock() {
        val block = createBlock(1, "1")
        storageInternal.addBlock(block)
        var map = storageInternal.getBlockAll()

        Assertions.assertEquals(map.size, 1)
        Assertions.assertEquals(map[1]!!.size, 1)
        Assertions.assertEquals(map[1]!![0].hash, "1")
        Assertions.assertEquals(map[1]!![0].height, 1)

        storageInternal.addBlock(createBlock(2, "2"))
        map = storageInternal.getBlockAll()

        Assertions.assertEquals(map.size, 2)
    }

    @Test
    fun removeBlockByHeight() {
        storageInternal.addBlock(createBlock(1, "11"))
        storageInternal.addBlock(createBlock(1, "12"))
        storageInternal.addBlock(createBlock(2, "21"))
        storageInternal.addBlock(createBlock(2, "22"))
        storageInternal.addBlock(createBlock(2, "23"))
        var map = storageInternal.getBlockAll()

        Assertions.assertEquals(map.size, 2)

        storageInternal.removeBlockByHeight(1)
        map = storageInternal.getBlockAll()

        Assertions.assertEquals(map.size, 1)

        storageInternal.removeBlockByHeight(2)
        map = storageInternal.getBlockAll()

        Assertions.assertEquals(map.size, 0)
    }

    @Test
    fun removeBlockByHash() {
        storageInternal.addBlock(createBlock(1, "11"))
        storageInternal.addBlock(createBlock(1, "12"))
        storageInternal.addBlock(createBlock(2, "21"))
        storageInternal.addBlock(createBlock(2, "22"))
        storageInternal.addBlock(createBlock(2, "23"))
        var map = storageInternal.getBlockAll()

        Assertions.assertEquals(map.size, 2)
        Assertions.assertEquals(map[1]!!.size, 2)

        storageInternal.removeBlockByHash("11")
        map = storageInternal.getBlockAll()

        Assertions.assertEquals(map.size, 2)
        Assertions.assertEquals(map[1]!!.size, 1)

        storageInternal.removeBlockByHash("12")
        map = storageInternal.getBlockAll()

        Assertions.assertEquals(map.size, 1)
    }

    @Test
    fun removeBlockByHashRange() {
        storageInternal.addBlock(createBlock(1, "11"))
        storageInternal.addBlock(createBlock(1, "12"))
        storageInternal.addBlock(createBlock(2, "21"))
        storageInternal.addBlock(createBlock(2, "22"))
        storageInternal.addBlock(createBlock(2, "23"))
        var map = storageInternal.getBlockAll()

        Assertions.assertEquals(map.size, 2)
        Assertions.assertEquals(map[1]!!.size, 2)

        storageInternal.removeBlockByHashRange(listOf("11", "21", "22"))
        map = storageInternal.getBlockAll()

        Assertions.assertEquals(map.size, 2)
        Assertions.assertEquals(map[1]!!.size, 1)
        Assertions.assertEquals(map[1]!![0].hash, "12")
        Assertions.assertEquals(map[2]!!.size, 1)
        Assertions.assertEquals(map[2]!![0].hash, "23")
    }

    @Test
    fun removeBlockByHeightRange() {
        storageInternal.addBlock(createBlock(1, "11"))
        storageInternal.addBlock(createBlock(1, "12"))
        storageInternal.addBlock(createBlock(2, "21"))
        storageInternal.addBlock(createBlock(2, "22"))
        storageInternal.addBlock(createBlock(2, "23"))
        storageInternal.addBlock(createBlock(3, "31"))
        storageInternal.addBlock(createBlock(3, "32"))
        var map = storageInternal.getBlockAll()

        Assertions.assertEquals(map.size, 2)
        Assertions.assertEquals(map[1]!!.size, 2)

        storageInternal.removeBlockByHeightRange(0, 1)
        map = storageInternal.getBlockAll()

        Assertions.assertEquals(map.size, 2)

        storageInternal.removeBlockByHeightRange(2, 3)
        map = storageInternal.getBlockAll()

        Assertions.assertEquals(map.size, 0)
    }

    @Test
    fun getBlockAll() {
    }

    @Test
    fun getBlockRange() {
    }

    @Test
    fun getBlock() {
        storageInternal.addBlock(createBlock(1, "1"))
        storageInternal.addBlock(createBlock(2, "2"))
        var block = storageInternal.getBlock("1")

        Assertions.assertNotEquals(block, null)
        Assertions.assertEquals(block?.height, 1)

        storageInternal.removeBlockByHash("1")
        block = storageInternal.getBlock("1")

        Assertions.assertEquals(block, null)
    }

    @Test
    fun addTransaction() {
    }

    @Test
    fun removeTransaction() {
    }

    @Test
    fun getTransaction() {
    }

    @Test
    fun addUtxo() {
        storageInternal.addUtxo(createUtxo("1", "1"))
        var set = storageInternal.getUtxoAll()

        Assertions.assertEquals(set.size, 1)

        storageInternal.addUtxo(createUtxo("2", "2"))
        storageInternal.addUtxo(createUtxo("2", "3"))
        set = storageInternal.getUtxoAll()

        Assertions.assertEquals(set.size, 2)
    }

    @Test
    fun removeUtxo() {
        storageInternal.addUtxo(createUtxo("1", "1"))
        storageInternal.addUtxo(createUtxo("2", "2"))
        storageInternal.addUtxo(createUtxo("2", "3"))
        var set = storageInternal.getUtxoAll()

        Assertions.assertEquals(set.size, 3)

        storageInternal.removeUtxo(createUtxo("1", "1"))
        set = storageInternal.getUtxoAll()

        Assertions.assertEquals(set.size, 2)

        storageInternal.removeUtxo(createUtxo("2", "2"))
        set = storageInternal.getUtxoAll()

        Assertions.assertEquals(set.size, 1)

        storageInternal.removeUtxo(createUtxo("2", "1"))
        set = storageInternal.getUtxoAll()

        Assertions.assertEquals(set.size, 1)

        storageInternal.removeUtxo(createUtxo("2", "3"))
        set = storageInternal.getUtxoAll()

        Assertions.assertEquals(set.size, 0)
    }

    @Test
    fun getUtxoList() {
    }

    @Test
    fun getAddress() {
    }


    @Test
    fun block() {
        val list = ArrayList<Transaction>()
        val transaction  = Transaction()
        list.add(transaction)
        val block = Block(10, list, Date().time, "123123", 0, 1001000);

        val hash = block.updateBlockHash()
        val hash1 = block.updateBlockHash()
        Assertions.assertEquals(hash, hash1)

        storageInternal.addBlock(block)

        val block1 = storageInternal.getBlock(hash)!!

        block1.updateBlockHash()

        Assertions.assertEquals(hash, block1.hash)
    }
}