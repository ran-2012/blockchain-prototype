package blockchain.storage;

import blockchain.data.core.BlockData;
import blockchain.data.core.TransactionData;

import java.util.List;
import java.util.Map;

public interface IStorage {

    void addBlockSync(BlockData data);

    void removeBlockSync(String blockId);

    void removeBlockRangeSync(Long min, Long max);

    Map<Long, List<BlockData>> getBlockAllSync();

    Map<Long, List<BlockData>> getBlockRangeSync(Long min, Long max);

    List<BlockData> getBlockSync();

    void addTransactionSync(TransactionData data);

    void removeTransactionSync(String transactionId);

    List<TransactionData> getTransactionSync(String pubKey);

}
