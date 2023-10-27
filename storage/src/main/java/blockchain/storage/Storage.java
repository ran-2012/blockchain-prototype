package blockchain.storage;

import blockchain.data.core.BlockData;
import blockchain.data.core.TransactionData;

import java.util.List;
import java.util.Map;

public class Storage implements IStorage {

    @Override
    public void addBlockSync(BlockData data) {

    }

    @Override
    public void removeBlockSync(String blockId) {

    }

    @Override
    public void removeBlockRangeSync(Long min, Long max) {

    }

    @Override
    public Map<Long, List<BlockData>> getBlockAllSync() {
        return null;
    }

    @Override
    public Map<Long, List<BlockData>> getBlockRangeSync(Long min, Long max) {
        return null;
    }

    @Override
    public List<BlockData> getBlockSync() {
        return null;
    }

    @Override
    public void addTransactionSync(TransactionData data) {

    }

    @Override
    public void removeTransactionSync(String transactionId) {

    }

    @Override
    public List<TransactionData> getTransactionSync(String pubKey) {
        return null;
    }
}
