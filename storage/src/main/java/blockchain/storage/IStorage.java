package blockchain.storage;

import blockchain.data.core.Block;
import blockchain.data.core.Transaction;
import blockchain.data.core.Utxo;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IStorage {

    void addBlock(Block data);

    void removeBlockByHeight(long height);

    void removeBlockByHash(String hash);

    void removeBlockByHashRange(List<String> hashes);

    void removeBlockByHeightRange(Long heightMin, Long heightMax);

    /**
     * Get all blocks.
     *
     * @return Map of height->block
     */
    Map<Long, List<Block>> getBlockAll();

    /**
     * Get blocks by height.
     *
     * @param heightMin Height min
     * @param heightMax Height max
     * @return Map of height->block
     */
    Map<Long, List<Block>> getBlockRange(Long heightMin, Long heightMax);

    @Nullable
    Block getBlock(String hash);

    List<Block> getLastBlock();

    void addTransaction(Transaction data);

    void removeTransaction(String hash);

    List<Transaction> getTransaction(String sourceAddress);

    void addUtxo(Utxo data);

    void removeUtxo(Utxo data);

    Set<Utxo> getUtxoByAddress(String address);

    Set<String> getAddressAll();

    Set<Utxo> getUtxoAll();

    void setHeight(long height);

    long getHeight();
}
