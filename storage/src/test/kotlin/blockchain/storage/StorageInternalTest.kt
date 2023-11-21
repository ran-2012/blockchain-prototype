package blockchain.storage

import blockchain.data.core.Block
import blockchain.data.core.Transaction
import blockchain.data.core.TransactionOutput
import org.junit.jupiter.api.*
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

    private fun createUtxo(address: String, value: Long): TransactionOutput {
        return TransactionOutput(address, value)
    }


    @Test
    fun addBlock() {
        val block = createBlock(1, "1")
        storageInternal.addBlock(block)
        var map = storageInternal.getBlockAll()

        Assertions.assertEquals(map.size, 1)

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
        storageInternal.addBlock(createBlock(2, "21"))
        var map = storageInternal.getBlockAll()

        Assertions.assertEquals(map.size, 2)

        storageInternal.removeBlock("11")
        map = storageInternal.getBlockAll()

        Assertions.assertEquals(map.size, 1)
    }

    @Test
    fun removeBlockByHashRange() {
        storageInternal.addBlock(createBlock(1, "11"))
        storageInternal.addBlock(createBlock(2, "21"))
        var map = storageInternal.getBlockAll()

        Assertions.assertEquals(map.size, 2)

        storageInternal.removeBlockByHashRange(listOf("11"))
        map = storageInternal.getBlockAll()

        Assertions.assertEquals(map.size, 1)
    }

    @Test
    fun removeBlockByHeightRange() {
        storageInternal.addBlock(createBlock(1, "11"))
        storageInternal.addBlock(createBlock(2, "21"))
        storageInternal.addBlock(createBlock(3, "31"))
        var map = storageInternal.getBlockAll()

        Assertions.assertEquals(map.size, 2)

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

        storageInternal.removeBlock("1")
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
    }

    @Test
    fun removeUtxo() {
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
        val transaction = Transaction()
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