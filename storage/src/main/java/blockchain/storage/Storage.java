package blockchain.storage;

public class Storage {

    private static IStorage instance;
    private static IStorage globalInstance;

    public static void initialize(String dbName) {
        instance = new StorageInternal(dbName, false);
        globalInstance = new StorageInternal(dbName, true);
    }

    public static IStorage getInstance() {
        if (instance == null) {
            throw new RuntimeException("Storage instance not initialized");
        }
        return instance;
    }

    public static IStorage getGlobalInstance() {
        return globalInstance;
    }

    private Storage() {
        throw new RuntimeException("Use getInstance instead");
    }
}
