/**
 * 区块链核心服务
 *
 * @author YUAN Longhang
 */
package blockchain.mining;

import blockchain.data.core.Block;
import blockchain.data.core.Transaction;
import blockchain.data.core.TransactionInput;
import blockchain.data.core.TransactionOutput;
import blockchain.network.INetwork;
import blockchain.network.Network;
import blockchain.storage.IStorage;
import blockchain.storage.Storage;
import blockchain.utility.Hash;
import blockchain.utility.Log;
import blockchain.utility.Rsa;
import org.jetbrains.annotations.TestOnly;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("FieldCanBeLocal")
public class BlockService {

    Log log = Log.get(this);

    public static float FEE_INDEX = 0.1f;

    public static long REWARD_PER_BLOCK = 100_000;

    public static long CHECK_TRANSACTION_POOL_INTERVAL = 1_000; // ms

    public static long TARGET_TIME_PER_BLOCK = 5_000; // ms
    public static int DEFAULT_DIFFICULTY = 16;
    public static int ADJUST_DIFFICULTY_BLOCK_INTERVAL = 10;
    public static long TARGET_TIME = TARGET_TIME_PER_BLOCK * ADJUST_DIFFICULTY_BLOCK_INTERVAL;

    private final IStorage storage = Storage.getInstance();
    private final INetwork network = Network.getInstance();

    private final MiningService miningService = MiningService.getInstance();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final Internal internal = new Internal();

    private final AtomicBoolean running = new AtomicBoolean(false);

    private final Boolean isMiner;
    private final String publicKey;
    private final String address;

    private final Runnable checkTransactionPoolRunnable = () -> {
        if (!running.get()) {
            return;
        }

        if (miningService.isRunning()) {
            return;
        }

        if (checkTransactionPool()) {
            List<Transaction> list = pollTransaction();
            Block block = generatePreMinedBlock(list);
            startMining(block);
        }
        scheduleCheckTransactionPool();
    };

    public BlockService() {
        this(false, "");
    }

    public BlockService(boolean isMiner, String publicKey) {
        this.isMiner = isMiner;
        this.publicKey = publicKey;
        this.address = Hash.hashString(publicKey);

        Network.getInstance().registerCallback(networkCallback);

    }

    public void start() {
        log.info("Starting block service");

        running.set(true);

        // Remove transaction and utxo
        storage.cleanCache();

        Map<Long, Block> blockAll = storage.getBlockAll();
        log.info("Loading block, count: {}", blockAll.size());
        for (long i = 0; i < blockAll.size(); ++i) {
            Block block = blockAll.get(i);
            assert block != null;

            initCacheForBlock(block);
        }
        log.info("Blocks loaded");

        if (isMiner) {
            log.info("Start checking transaction pool");
            assert !publicKey.isEmpty();
            miningService.setCallback(callback);
            scheduleCheckTransactionPool();
        }
    }

    public void stop() {
        log.info("Stopping block service");
        running.set(false);
        miningService.stop();
        try {
            if (!executor.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                log.warn("Time out, force stopping");
                executor.shutdownNow();
            }
        } catch (InterruptedException ignored) {
        }
    }

    public long getFee(long value) {
        return (long) (value * FEE_INDEX);
    }

    private void addNewBlock(Block block) {
        executor.execute(() -> internal.addNewBlock(block));
    }

    private void addNewTransaction(Transaction transaction) {
        executor.execute(() -> internal.addNewTransaction(transaction));
    }

    private void scheduleCheckTransactionPool() {
        executor.schedule(checkTransactionPoolRunnable, CHECK_TRANSACTION_POOL_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private boolean checkTransactionPool() {
        long currentTime = new Date().getTime();

        Block lastBlock = storage.getLastBlock();
        if (lastBlock == null) {
            return true;
        }

        long previousBlockTime = lastBlock.getTimestamp();
        long timeDifference = currentTime - previousBlockTime;
        return timeDifference > 2_000 && !storage.getTransactionAll().isEmpty();
    }

    private List<Transaction> pollTransaction() {
        // Maybe just return all transactions
        return storage.getTransactionAll();
    }

    private int getDifficulty(long timestamp) {
        Block previousBlock = storage.getLastBlock();
        long nextHeight = previousBlock == null ? 0 : storage.getHeight() + 1;
        // First block in block 0 (pre-generated)
        // Only adjust after 10 blocks
        if (nextHeight < ADJUST_DIFFICULTY_BLOCK_INTERVAL) {
            return DEFAULT_DIFFICULTY;
        }
        int currentDifficulty = previousBlock == null ? DEFAULT_DIFFICULTY : previousBlock.getDifficulty();
        // Adjust difficulty every 10 blocks
        if (nextHeight % ADJUST_DIFFICULTY_BLOCK_INTERVAL == 0) {
            long preHeight = nextHeight - ADJUST_DIFFICULTY_BLOCK_INTERVAL;

            Map<Long, Block> map = storage.getBlockRange(preHeight, nextHeight);
            Block preBlock = map.get(preHeight);

            assert preBlock != null;

            long timeDelta = timestamp - preBlock.getTimestamp();

            assert timeDelta > 0;

            if (timeDelta / TARGET_TIME > 1) {
                currentDifficulty -= (int) (Math.log((double) timeDelta / TARGET_TIME) / Math.log(2));
                if (currentDifficulty < 0) {
                    currentDifficulty = 0;
                }
                log.debug("Difficulty changed to: {}", currentDifficulty);
            } else if (TARGET_TIME / timeDelta > 1) {
                currentDifficulty += (int) (Math.log((double) TARGET_TIME / timeDelta) / Math.log(2));
                log.debug("Difficulty changed to: {}", currentDifficulty);
            }
        }
        return currentDifficulty;
    }

    private Block generatePreMinedBlock(List<Transaction> transactions) {
        log.debug("Pre-generate block with {} transactions", transactions.size());

        Block previousBlock = storage.getLastBlock();

        // Generate block here then mine
        Block block = new Block();

        if (previousBlock == null) {
            log.info("No existing block, generating block 0");
            block.setHeight(0);
            block.setPrevHash("");
        } else {
            block.setHeight(previousBlock.getHeight() + 1);
            block.setPrevHash(previousBlock.getHash());
        }

        long timestamp = new Date().getTime();
        block.setTimestamp(timestamp);
        block.setDifficulty(getDifficulty(timestamp));
        long fee = 0;
        for (Transaction transaction : transactions) {
            block.addTransaction(transaction);
            fee += transaction.fee;
        }
        TransactionOutput coinBaseOutput = new TransactionOutput(this.address, fee + REWARD_PER_BLOCK);
        Transaction coinBaseTransaction = new Transaction(coinBaseOutput);
        block.addTransaction(coinBaseTransaction);
        block.updateMerkleRoot();
        block.setNonce(0);
        return block;
    }

    private void startMining(Block block) {
        miningService.setBlock(block);
        miningService.start();
    }

    private boolean checkUtxo(Transaction transaction) {
        for (TransactionInput input : transaction.inputs) {
            if (!storage.hasUtxo(input)) {
                log.warn("Utxo {} is already in use", input);
                return false;
            }
        }
        return true;
    }

    private boolean checkSignature(Transaction transaction) {
        for (TransactionInput input : transaction.inputs) {
            String signature = input.signature;
            input.signature = "";
            if (!Rsa.verify(input, signature, input.publicKey)) {
                input.signature = signature;
                log.warn("Fail to verify input {}", input);
                return false;
            }
            input.signature = signature;
        }
        if (!Rsa.verify(transaction.outputHash, transaction.outputSignature, transaction.sourcePublicKey)) {
            log.warn("Failed to verify output");
            return false;
        }
        return true;
    }

    private void initCacheForBlock(Block block) {
        for (Transaction transaction : block.getData()) {
            initCacheForCompletedTransaction(transaction);
        }
    }

    private void initCacheForCompletedTransaction(Transaction transaction) {
        storage.removeTransaction(transaction.hash);
        storage.addUtxoFromTransactionOutput(transaction);
        storage.removeUtxoFromTransactionInput(transaction);
    }

    /**
     * Functions in this class in run on single thread to ensure thread-safety.<p>
     * Use <code>execute.schedule(new Runnable())</code> to run these functions.
     */
    private class Internal {

        private void updateChainTillHashMatches(Block block) {
            Map<Long, Block> blockToAddList = new HashMap<>();
            blockToAddList.put(block.getHeight(), block);

            long targetHeight = block.getHeight() - 1;
            String targetHash = block.getPrevHash();

            // To accept the new block, search where two chains are diverged
            while (targetHeight >= 0 && storage.getBlock(targetHash) == null) {
                log.debug("Target hash: {}, target height: {}", targetHash, targetHeight);
                Map<String, Block> result = network.getBlock(targetHash);
                if (!result.isEmpty()) {
                    Block blockAtTargetHeight = null;
                    for (Block blk : result.values()) {
                        if (blk.getHash().equals(targetHash) && blk.getHeight() == targetHeight) {
                            try {
                                blk.validate();
                            } catch (Exception ignore) {
                            }
                            blockAtTargetHeight = blk;
                            break;
                        }
                    }
                    if (blockAtTargetHeight == null) {
                        log.warn("Unable to fetch block, hash: {}, height: {} from other node", targetHash, targetHeight);
                        return;
                    } else {
                        blockToAddList.put(targetHeight, blockAtTargetHeight);
                        --targetHeight;
                        targetHash = blockAtTargetHeight.getPrevHash();
                    }
                } else {
                    log.warn("Unable to fetch block, hash: {} from other node", block.getPrevHash());
                    log.warn("Reject block, hash: {}", block.getHash());
                    return;
                }
            }

            if (storage.getBlock(targetHash) != null) {
                log.info("Hash matches at height: {}", targetHeight);
            }

            // Revert local chain to target height which makes possible to accept incoming block
            while (storage.getHeight() > targetHeight) {
                revertBlock();
            }
            if (targetHeight == -1) {
                log.info("All block reverted, loading new chain");
            }

            assert block.getHeight() - targetHeight == blockToAddList.size();

            log.info("Add block from height {} to {}", targetHeight + 1, block.getHeight());
            // Some previous transaction may be requried to revert
//            List<Transaction> preTransactionList = storage.getTransactionAll();
            // Add blocks on the longer chain from peers
            for (long height = targetHeight + 1; height <= block.getHeight(); ++height) {
                Block blockToAdd = blockToAddList.get(height);
                assert blockToAdd != null;

                log.info("Add block height: {}, hash: {}", blockToAdd.getHeight(), blockToAdd.getHash());
                storage.addBlock(blockToAdd);
                for (Transaction transaction : blockToAdd.getData()) {
                    storage.removeTransaction(transaction.hash);
                    storage.addUtxoFromTransactionOutput(transaction);
                    storage.removeUtxoFromTransactionInput(transaction);
                    storage.removePendingUtxoFromTransactionInput(transaction);

//                    // Completed transaction from other peers may contain utxo already used
//                    Set<String> usedSet = new HashSet<>();
//                    for (TransactionInput usedInput : transaction.inputs) {
//                        usedSet.add(usedInput.originalTxHash + usedInput.originalOutputIndex);
//                    }
//                    for (Transaction tx : preTransactionList) {
//                        for (TransactionInput input : tx.inputs) {
//                            // If transaction in pool shared a utxo with completed tx, revert the tx in the pool
//                            if (usedSet.contains(input.originalTxHash + input.originalTxHash)) {
//                                storage.removeTransaction(tx.hash);
//                                storage.removePendingUtxoFromTransactionInput(tx);
//                                break;
//                            }
//                        }
//                    }
                }
            }
        }

        private void addNewBlock(Block block) {
            if (!running.get()) {
                return;
            }

            if (block.getHeight() <= storage.getHeight()) {
                log.info("Block height {} is smaller than current height {}, skip.",
                        block.getHeight(), storage.getHeight());
                return;
            }

            // Check block
            try {
                block.validate();
            } catch (Exception e) {
                log.warn("Invalid block: hash mismatch");
                log.warn(e);
                return;
            }
            updateChainTillHashMatches(block);
        }

        private void addNewTransaction(Transaction transaction) {
            if (!running.get()) {
                return;
            }

            log.info("New transaction received from {} to {}", transaction.sourceAddress, transaction.targetAddress);
            // Transaction may come from user and the hash is not updated
            transaction.updateHash();

            try {
                if (!checkUtxo(transaction)) {
                    log.warn("Invalid input list");
                    return;
                }
                if (!checkSignature(transaction)) {
                    log.warn("Transaction verification failed");
                    return;
                }
            } catch (Exception e) {
                log.warn(e);
                e.printStackTrace();
                log.warn("Unable to verify transaction {}", transaction.hash);
            }

            storage.addTransaction(transaction);
            storage.removeUtxoFromTransactionInput(transaction);
            storage.addPendingUtxoFromTransactionInput(transaction);

            log.info("New transaction added to pool, hash: {}", transaction.hash);
        }

        // Revert last block in database
        private void revertBlock() {
            Block block = storage.getLastBlock();
            if (block == null) {
                log.warn("All block reverted");
                return;
            }

            log.info("Reverting block, height: {}", block.getHeight());

            storage.removeBlock();

//            // Add transactions back into the pool
//            List<Transaction> transactions = block.getData();
//            for (Transaction transaction : transactions) {
//                if (transaction.isCoinbase()) {
//                    storage.removeUtxoFromTransactionOutput(transaction);
//                    continue;
//                }
//                revertTransactionFromBlock(transaction);
//            }
        }

        // Put transactions back to the pool
        private void revertTransactionFromBlock(Transaction transaction) {
            storage.removeUtxoFromTransactionOutput(transaction);
            storage.addPendingUtxoFromTransactionInput(transaction);
            storage.addTransaction(transaction);

            // Remove all transaction which contains output from the reverted transaction
            List<Transaction> transactions = storage.getTransactionAll();
            for (Transaction transaction1 : transactions) {
                for (TransactionInput input : transaction1.inputs) {
                    if (input.originalTxHash.equals(transaction.hash)) {
                        storage.removePendingUtxoFromTransactionInput(transaction);
                        storage.removeTransaction(transaction.hash);
                    }
                }
            }
        }
    }

    private final MiningService.Callback callback = new MiningService.Callback() {
        @Override
        public void onNewBlockMined(Block block) {
            log.info("New block mined, height: {}, hash: {}", block.getHeight(), block.getHash());
            log.info("Nonce: {}", block.getNonce());
            log.info("Time used: {} ms", System.currentTimeMillis() - block.getTimestamp());
            addNewBlock(block);
            log.debug("Broadcasting new block");
            network.newBlock(block);
        }

        @Override
        public void onAllNonceTried(Block block) {
            log.error("How is this possible?");
            System.exit(-1);
            // Empty
            // Unlikely to use up all nonce
        }
    };

    private final INetwork.Callback networkCallback = new INetwork.Callback() {
        @Override
        public void onNewBlockReceived(Block data) {
            addNewBlock(data);
        }

        @Override
        public Transaction onNewTransactionRequested(String sourceAddress, String targetAddress, long value) {
            return generateNewTransaction(sourceAddress, targetAddress, value);
        }

        Set<String> receivedTransaction = new HashSet<>();

        @Override
        public void onSignedTransactionReceived(Transaction transaction) {
            if (!receivedTransaction.contains(transaction.hash)) {
                log.debug("Broadcasting new transaction");
                network.newTransaction(transaction);
                receivedTransaction.add(transaction.hash);
            } else {
                return;
            }
            log.info("New transaction received, hash: {}", transaction.hash);

            // verification is in Internal.addNewTransaction
            addNewTransaction(transaction);
        }
    };

    @TestOnly
    public Transaction generateNewTransaction(String sourceAddress, String targetAddress, long value) {
        Set<TransactionInput> utxoList = storage.getUtxoByAddress(sourceAddress);
        List<TransactionInput> inputList = new ArrayList<>();
        List<TransactionOutput> outputList = new ArrayList<>();

        long requiredValue = value + getFee(value);
        long usedValue = 0L;
        for (TransactionInput utxo : utxoList) {
            usedValue += utxo.getValue();
            inputList.add(utxo);
            if (usedValue >= requiredValue) {
                break;
            }
        }
        if (usedValue < requiredValue) {
            // Reject the transaction if the source address does not have enough funds
            throw new RuntimeException("Not enough balance in " + sourceAddress);
        }

        // To targetAddress
        TransactionOutput output1 = new TransactionOutput(targetAddress, value);
        outputList.add(output1);

        if (usedValue > requiredValue) {
            // Send back additional money back
            TransactionOutput output2 = new TransactionOutput(sourceAddress, usedValue - requiredValue);
            outputList.add(output2);
        }
        // Generate a new transaction
        Transaction transaction = new Transaction(sourceAddress, targetAddress, inputList, outputList);

        log.info("Generate new transaction from: {}, to: {}, value: {}", sourceAddress, targetAddress, value);

        // Return the new transaction to the client
        return transaction;
    }
}