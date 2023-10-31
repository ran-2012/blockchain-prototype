package blockchain.storage;

import blockchain.data.core.Block;
import blockchain.data.core.Transaction;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IStorage {

    void addBlockSync(Block data);

    void removeBlockSync(long height);

    void removeBlockRangeSync(Long heightMin, Long heightMax);

    Map<Long, List<Block>> getBlockAllSync();

    Map<Long, List<Block>> getBlockRangeSync(Long heightMin, Long heightMax);

    Block getBlockSync(String hash);

    void addTransactionSync(Transaction data);

    void removeTransactionSync(String hash);

    List<Transaction> getTransactionSync(String sourceAddress);

    void addUtxoSync(String address, Transaction data);

    void removeUtxoSync(String address, Transaction data);

    Set<String> getUtxoListSync(String address);

    Set<String> getAddress();
}
