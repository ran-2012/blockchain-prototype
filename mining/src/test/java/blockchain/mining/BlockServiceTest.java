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
}