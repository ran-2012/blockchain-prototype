package blockchain.storage;

import blockchain.data.core.Block;
import blockchain.data.core.Transaction;
import blockchain.data.core.TransactionOutput;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IStorage {

    void addBlock(Block data);

    void removeBlockByHeight(long height);

    void removeBlock(String hash);

    void removeBlockByHashRange(List<String> hashes);

    void removeBlockByHeightRange(Long heightMin, Long heightMax);

    /**
     * Get all blocks.
     *
     * @return Map of height->block
     */
    Map<Long, Block> getBlockAll();

    /**
     * Get blocks by height.
     *
     * @param heightMin Height min
     * @param heightMax Height max
     * @return Map of height->block
     */
    Map<Long, Block> getBlockRange(Long heightMin, Long heightMax);

    @Nullable
    Block getBlock(String hash);

    Block getLastBlock();

    void addTransaction(Transaction data);

    void removeTransaction(String hash);

    List<Transaction> getTransactionAll(String sourceAddress);

    void addUtxoFromTransactionOutput(Transaction transaction);

    void removeUtxoFromTransactionInput(Transaction transaction);

    Set<TransactionOutput> getUtxoByAddress(String address);

    Set<String> getAddressAll();

    Set<TransactionOutput> getUtxoAll();

    void addPendingUtxoFromTransactionOutput(Transaction transaction);

    void removePendingUtxoFromTransactionInput(Transaction transaction);

    Set<TransactionOutput> getPendingUtxoAll();

    void setHeight(long height);

    long getHeight();
}
