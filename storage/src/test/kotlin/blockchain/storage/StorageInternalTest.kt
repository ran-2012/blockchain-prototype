package blockchain.storage

import blockchain.data.core.Block
import blockchain.data.core.Transaction
import blockchain.data.core.TransactionInput
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

    private fun createTransactionOutput(address: String, value: Long): TransactionOutput {
        return TransactionOutput(address, value)
    }

    private fun createTransactionInput(
        address: String,
        value: Long,
        originTxHash: String,
        originTxOutputIdx: Int
    ): TransactionInput {
        return TransactionInput(address, value, originTxHash, originTxOutputIdx, "", "")
    }

    private fun createTransaction(): Transaction {
        val inList = ArrayList<TransactionInput>()
        inList.add(createTransactionInput("1", 1, "1", 0));
        inList.add(createTransactionInput("1", 2, "2", 0));
        inList.add(createTransactionInput("1", 3, "2", 1));

        val outList = ArrayList<TransactionOutput>()
        outList.add(createTransactionOutput("2", 5))
        outList.add(createTransactionOutput("1", 1))

        val transaction = Transaction(inList, outList)
        transaction.hash = "123"
        transaction.sourceAddress = "1"
        transaction.targetAddress = "2"

        return transaction
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

        Assertions.assertEquals(map.size, 3)

        storageInternal.removeBlockByHeightRange(0, 1)
        map = storageInternal.getBlockAll()

        Assertions.assertEquals(map.size, 2)

        storageInternal.removeBlockByHeightRange(2, 3)
        map = storageInternal.getBlockAll()

        Assertions.assertEquals(map.size, 0)
    }

    @Test
    fun getBlockAll() {
        storageInternal.addBlock(createBlock(1, "11"))
        storageInternal.addBlock(createBlock(2, "21"))
        storageInternal.addBlock(createBlock(3, "31"))
        val map = storageInternal.getBlockAll()

        Assertions.assertEquals(map.size, 3)
    }

    @Test
    fun getBlockRange() {
        storageInternal.addBlock(createBlock(1, "11"))
        storageInternal.addBlock(createBlock(2, "21"))
        storageInternal.addBlock(createBlock(3, "31"))
        var map = storageInternal.getBlockRange(2, 4)

        Assertions.assertEquals(map.size, 2)

        map = storageInternal.getBlockRange(4, 5)

        Assertions.assertEquals(map.size, 0)
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

    @Test
    fun addUtxoFromTransactionOutput() {
        val transaction = createTransaction()

        storageInternal.addUtxoFromTransactionOutput(transaction)

        Assertions.assertEquals(2, storageInternal.utxoAll.size)
        Assertions.assertEquals(1, storageInternal.getUtxoByAddress("1").size)
        Assertions.assertEquals(1, storageInternal.getUtxoByAddress("2").size)
    }

    @Test
    fun removeUtxoFromTransactionInput() {
        val originTx = createTransaction()
        storageInternal.addUtxoFromTransactionOutput(originTx)

        val inList = ArrayList<TransactionInput>()
        inList.add(createTransactionInput("2", 5, originTx.hash, 0))
        val transaction = Transaction()
        transaction.sourceAddress = "2"
        transaction.inputs = inList

        storageInternal.removeUtxoFromTransactionInput(transaction)

        Assertions.assertEquals(1, storageInternal.utxoAll.size)
        Assertions.assertEquals(0, storageInternal.getUtxoByAddress("2").size)
    }

    @Test
    fun addPendingUtxoFromTransactionInput() {
        val transaction = createTransaction()
        storageInternal.addPendingUtxoFromTransactionInput(transaction)

        Assertions.assertEquals(transaction.inputs.size, storageInternal.pendingUtxoAll.size)
    }

    @Test
    fun removePendingUtxoFromTransactionInput() {
        val transaction = createTransaction()
        storageInternal.addPendingUtxoFromTransactionInput(transaction)

        Assertions.assertEquals(transaction.inputs.size, storageInternal.pendingUtxoAll.size)

        storageInternal.removePendingUtxoFromTransactionInput(transaction)

        Assertions.assertEquals(0, storageInternal.pendingUtxoAll.size)
    }
}