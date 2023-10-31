package blockchain.storage;

import blockchain.data.core.Transaction;

import java.util.List;
import java.util.Map;

public class Storage implements IStorage {

    @Override
    public void addBlockSync(Block data) {

    }

    @Override
    public void removeBlockSync(String height) {

    }

    @Override
    public void removeBlockRangeSync(Long heightMin, Long heightMax) {

    }

    @Override
    public Map<Long, List<Block>> getBlockAllSync() {
        return null;
    }

    @Override
    public Map<Long, List<Block>> getBlockRangeSync(Long heightMin, Long heightMax) {
        return null;
    }

    @Override
    public List<Block> getBlockSync() {
        return null;
    }

    @Override
    public void addTransactionSync(Transaction data) {

    }

    @Override
    public void removeTransactionSync(String transactionId) {

    }

    @Override
    public List<Transaction> getTransactionSync(String pubKey) {
        return null;
    }
}
