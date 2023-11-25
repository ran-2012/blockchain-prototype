package blockchain.mining;

import blockchain.data.core.Transaction;
import blockchain.data.core.TransactionInput;
import blockchain.data.core.TransactionOutput;
import blockchain.network.Network;
import blockchain.storage.Storage;
import blockchain.storage.StorageInternal;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlockServiceTest {

    BlockService service = new BlockService();
    static StorageInternal storageInternal;

    @BeforeAll
    static void setUpAll() {
        Network.init(7070, new HashMap<>());
        Storage.initialize("test");
        storageInternal = (StorageInternal) Storage.getInstance();
    }

    @AfterAll
    static void tearDownAll() {
        storageInternal.cleanUp();
    }

    @BeforeEach
    void setUp() {
        storageInternal.cleanUp();
    }

    @AfterEach
    void tearDown() {
    }

    private void addUtxo(String address, String txId, int idx, long value) {
        storageInternal.addUtxo(txId, idx, new TransactionOutput(address, value));
    }

    @Test
    void generateNewTransactionTest() {
        addUtxo("1", "99", 0, 5);
        addUtxo("1", "100", 0, 100);
        addUtxo("1", "101", 0, 50);
        addUtxo("1", "102", 0, 100);

        List<Long> testValue = Arrays.asList(100L, 101L, 150L, 10L, 25L);

        for (long value : testValue) {
            Transaction tx = service.generateNewTransaction("1", "2", value);
            long inputValue = 0;
            long outputValue = 0;
            long toSourceValue = 0;
            long toTargetValue = 0;
            for (TransactionInput input : tx.inputs) {
                inputValue += input.value;
            }
            for (TransactionOutput output : tx.outputs) {
                outputValue += output.value;
                if (output.address.equals("1")) {
                    toSourceValue += output.value;
                }
                if (output.address.equals("2")) {
                    toTargetValue += output.value;
                }
            }

            Assertions.assertEquals(tx.fee, inputValue - outputValue);
            Assertions.assertEquals(service.getFee(value), tx.fee);
            Assertions.assertEquals(value, toTargetValue);
            Assertions.assertEquals(inputValue - toTargetValue - tx.fee, toSourceValue);
        }

        Assertions.assertThrows(RuntimeException.class, () -> {
            service.generateNewTransaction("1", "2", 500);
        });
    }
}