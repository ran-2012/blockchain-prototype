package blockchain.mining;

import blockchain.data.core.Block;
import blockchain.data.core.Transaction;
import blockchain.storage.IStorage;
import blockchain.storage.Storage;

public class BlockCache {
    public static void addBlockSync(Block newBlock) {
        // 获取存储实例
        IStorage storage = Storage.getInstance();
        //存储区块
        storage.addBlock(newBlock);
    }

    public static void addTransactionSync(Transaction transaction) {
        // 获取存储实例
        IStorage storage = Storage.getInstance();
        // 同步地添加交易到存储中
        storage.addTransaction(transaction);
    }
}
