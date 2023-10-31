package blockchain.storage;

public class Storage {

    private static IStorage instance;

    public static void initialize(String dbName) {
        instance = new StorageInternal(dbName);
    }

    public static IStorage getInstance() {
        if (instance == null) {
            throw new RuntimeException("Storage instance not initialized");
        }
        return instance;
    }

    private Storage() {
        throw new RuntimeException("Use getInstance instead");
    }
}
