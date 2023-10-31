package blockchain.storage;

import blockchain.data.core.Transaction;

import java.util.List;
import java.util.Map;

public interface IStorage {

    void addBlockSync(Block data);

    void removeBlockSync(String height);

    void removeBlockRangeSync(Long heightMin, Long heightMax);

    Map<Long, List<Block>> getBlockAllSync();

    Map<Long, List<Block>> getBlockRangeSync(Long heightMin, Long heightMax);

    List<Block> getBlockSync();

    void addTransactionSync(Transaction data);

    void removeTransactionSync(String transactionId);

    List<Transaction> getTransactionSync(String pubKey);

}
