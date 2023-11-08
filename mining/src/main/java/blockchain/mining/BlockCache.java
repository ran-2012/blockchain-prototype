package blockchain.mining;
import blockchain.data.core.Block;
import blockchain.data.core.Transaction;
import blockchain.storage.IStorage;
import blockchain.storage.Storage;

import java.util.ArrayList;

public class BlockCache {
/**
 * 初始化存储实例
 */
    blockchain.storage.Storage.initialize("thisDatabase");
    public static void addBlockSync(Block newBlock){
        // 获取存储实例
        IStorage storage = Storage.getInstance();
        //存储区块
        storage.addBlockSync(newBlock);
        System.out.println("新区块存储完成");
    }

    public static void addTransactionSync(Transaction transaction){
        // 获取存储实例
        IStorage storage = Storage.getInstance();
        // 同步地添加交易到存储中
        storage.addTransactionSync(transaction);
    }
}
