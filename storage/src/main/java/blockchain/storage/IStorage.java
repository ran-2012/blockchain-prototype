package blockchain.storage;

import blockchain.data.core.Block;
import blockchain.data.core.Transaction;
import blockchain.data.core.TransactionInput;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IStorage {

    void addBlock(Block data);

    /**
     * Remove last block
     */
    void removeBlock();

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

    @Nullable
    Block getLastBlock();

    void addTransaction(Transaction data);

    void removeTransaction(String hash);

    @Nullable
    Transaction getTransaction(String sourceAddress);

    List<Transaction> getTransactionAll();

    void addUtxoFromTransactionOutput(Transaction transaction);

    void removeUtxoFromTransactionInput(Transaction transaction);

    void removeUtxoFromTransactionOutput(Transaction transaction);

    boolean hasUtxo(TransactionInput utxo);

    Set<TransactionInput> getUtxoByAddress(String address);

    Set<String> getAddressAll();

    Set<TransactionInput> getUtxoAll();

    void addPendingUtxoFromTransactionInput(Transaction transaction);

    void removePendingUtxoFromTransactionInput(Transaction transaction);

    boolean hasPendingUtxo(TransactionInput utxo);

    Set<TransactionInput> getPendingUtxoAll();

    long getHeight();

    /**
     * Remove all transaction and utxo
     */
    void cleanCache();
}
