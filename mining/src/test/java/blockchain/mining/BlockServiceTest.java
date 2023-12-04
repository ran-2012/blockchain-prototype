package blockchain.mining;

import blockchain.network.Network;
import blockchain.storage.Storage;
import blockchain.storage.StorageInternal;
import org.junit.jupiter.api.*;

import java.util.HashMap;

class BlockServiceTest {

    LocalBlockService service = new LocalBlockService();
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