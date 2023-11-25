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
import blockchain.data.exceptions.AlreadyMinedException;
import blockchain.network.INetwork;
import blockchain.network.Network;
import blockchain.storage.IStorage;
import blockchain.storage.Storage;
import blockchain.utility.Hash;
import blockchain.utility.Log;
import blockchain.utility.Rsa;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("FieldCanBeLocal")
public class BlockService {

    Log log = Log.get(this);


    public static long CHECK_TRANSACTION_POOL_INTERVAL = 1000;

    public static long TARGET_TIME_PER_BLOCK = 1_000;
    public static int DEFAULT_DIFFICULTY = 20;
    public static int ADJUST_DIFFICULTY_BLOCK_INTERVAL = 10;
    public static long TARGET_TIME = TARGET_TIME_PER_BLOCK * ADJUST_DIFFICULTY_BLOCK_INTERVAL;

    private int currentDifficulty = DEFAULT_DIFFICULTY;

    private final IStorage storage = Storage.getInstance();
    private final INetwork network = Network.getInstance();

    private final MiningService miningService = MiningService.getInstance();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final Internal internal = new Internal();

    private final Runnable checkTransactionPoolRunnable = () -> {
        if (miningService.isRunning()) {
            return;
        }

        if (checkTransactionPool()) {
            List<Transaction> list = pollTransaction();
            Block block = generateNewBlock(list);
            startMining(block);
        }
        scheduleCheckTransactionPool();
    };

    public BlockService(boolean startMining) {
        Network.getInstance().registerCallback(networkCallback);
        miningService.setCallback(callback);
        if (startMining) {
            scheduleCheckTransactionPool();
        }
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
        // TODO: Check the time between previous block and current time.
        long currentTime = new Date().getTime();
        Block lastBlock = storage.getLastBlock();
        long previousBlockTime = lastBlock.getTimestamp();
        long timeDifference = currentTime - previousBlockTime;
        return timeDifference > 60000;
    }

    private List<Transaction> pollTransaction() {
        // TODO: Maybe not just return all transactions?
        return storage.getTransactionAll();
    }

    private int getDifficult(long timestamp) {
        long nextHeight = storage.getHeight() + 1;
        // First block in block 0 (pre-generated)
        // Only adjust after 10 blocks
        if (nextHeight < ADJUST_DIFFICULTY_BLOCK_INTERVAL) {
            return DEFAULT_DIFFICULTY;
        }
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
            } else if (TARGET_TIME / timeDelta > 1) {
                currentDifficulty += (int) (Math.log((double) TARGET_TIME / timeDelta) / Math.log(2));
            }
        }
        return currentDifficulty;
    }

    private Block generateNewBlock(List<Transaction> transactions) {
        // Generate block here then mine
        Block block = new Block();
        block.setPrevHash(storage.getLastBlock().getHash());
        long timestamp = new Date().getTime();
        block.setTimestamp(timestamp);
        block.setDifficulty(getDifficult(timestamp));
        for (Transaction transaction : transactions) {
            try {
                block.addTransaction(transaction);
            } catch (AlreadyMinedException ignored) {
            }
        }
        block.setNonce(0);
        block.setHeight(storage.getLastBlock().getHeight() + 1);
        block.setHash(Hash.hashString(block.toString()));
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

    /**
     * Functions in this class in run on single thread to ensure thread-safety.<p>
     * Use <code>execute.schedule(new Runnable())</code> to run these functions.
     */
    private class Internal {

        private Map<Long, Block> getBlocksUntilHashMatches(Block block) {
            Map<Long, Block> result = new HashMap<>();
            if (block.getHeight() > storage.getHeight()) {
                Map<String, Map<Long, Block>> map = network.getBlockRange(storage.getHeight(), block.getHeight());
            }
            return result;
        }

        private void addNewBlock(Block block) {
            if (block.getHeight() <= storage.getHeight()) {
                log.info("Block height {} is smaller than current height {}, skip.",
                        block.getHeight(), storage.getHeight());
                return;
            }


            // Check block
            try {
                block.validate();
            } catch (Exception e) {
                log.error("Invalid block: hash mismatch");
                return;
            }
            if (block.getHeight() == storage.getHeight() + 1) {
                Block lastBlock = storage.getLastBlock();
                if (!block.getPrevHash().equals(lastBlock.getHash())) {
                    log.error("Invalid block: previous hash mismatch");
                    return;
                }
                // Add block to database
                storage.addBlock(block);
                storage.setHeight(block.getHeight());
            } else {

            }

        }

        private void addNewTransaction(Transaction transaction) {
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
                log.error(e);
                return;
            }

            storage.removeUtxoFromTransactionInput(transaction);
            storage.addPendingUtxoFromTransactionInput(transaction);
            storage.addTransaction(transaction);
        }

        // Revert last block in database
        private void revertBlock() {
            Block block = storage.getLastBlock();

            assert block != null;
            assert block.getHeight() > 0;

            // Add transactions back into the pool
            List<Transaction> transactions = block.getData();
            for (Transaction transaction : transactions) {
                revertTransactionFromBlock(transaction);
            }
        }

        // Recursively remove all transaction which contains output from the transaction
        private void removeRelatedTransactionFromPool(Transaction transaction) {
            List<Transaction> transactions = storage.getTransactionAll();
            for (Transaction transaction1 : transactions) {
                for (TransactionInput input : transaction1.inputs) {
                    if (input.originalTxHash.equals(transaction.hash)) {
                        revertTransactionFromPool(transaction);
                    }
                }
            }
        }

        // Put transactions back to the pool
        private void revertTransactionFromBlock(Transaction transaction) {
            storage.removeUtxoFromTransactionOutput(transaction);
            storage.addPendingUtxoFromTransactionInput(transaction);
            storage.addTransaction(transaction);

            removeRelatedTransactionFromPool(transaction);
        }

        // Remove transaction from the pool
        private void revertTransactionFromPool(Transaction transaction) {
            storage.removePendingUtxoFromTransactionInput(transaction);
            storage.removeTransaction(transaction.hash);

            removeRelatedTransactionFromPool(transaction);
        }
    }

    private final MiningService.Callback callback = new MiningService.Callback() {
        @Override
        public void onNewBlockMined(Block block) {
            addNewBlock(block);
            network.newBlock(block);
        }

        @Override
        public void onAllNonceTried(Block block) {
            block.setTimestamp(new Date().getTime());
            miningService.setBlock(block);
            miningService.start();
        }
    };

    private final INetwork.Callback networkCallback = new INetwork.Callback() {
        @Override
        public Block onBlockWithHashRequested(String hash) {
            return storage.getBlock(hash);
        }

        @Override
        public Block onBlockWithHeightRequested(Long height) {
            return storage.getBlockRange(height, height).get(height);
        }

        @Override
        public Map<Long, Block> onBlockRangeRequested(Long heightMin, Long heightMax) {
            return storage.getBlockRange(heightMin, heightMax);
        }

        @Override
        public void onNewBlockReceived(Block data) {
            if (miningService.getBlock().getHeight() == data.getHeight()) {
                miningService.stop();
            }
            addNewBlock(data);
        }

        @Override
        public Transaction onNewTransactionRequested(String sourceAddress, String targetAddress, long value) {
            Set<TransactionInput> utxoList = storage.getUtxoByAddress(sourceAddress);
            List<TransactionInput> inputList = new ArrayList<>();
            List<TransactionOutput> outputList = new ArrayList<>();

            long totalValue = 0L;
            for (TransactionInput utxo : utxoList) {
                totalValue += utxo.getValue();
                inputList.add(utxo);
                if (totalValue >= value) {
                    break;
                }
            }
            if (totalValue < value) {
                // Reject the transaction if the source address does not have enough funds
                throw new RuntimeException("Not enough balance in " + sourceAddress);
            }

            // To targetAddress
            TransactionOutput output1 = new TransactionOutput(targetAddress, value);
            outputList.add(output1);

            if (totalValue > value) {
                // Send back additional money back
                TransactionOutput output2 = new TransactionOutput(sourceAddress, totalValue - value);
                outputList.add(output2);
            }
            // Generate a new transaction
            Transaction transaction = new Transaction(inputList, outputList);
            transaction.sourceAddress = sourceAddress;
            transaction.targetAddress = targetAddress;

            log.info("Generate new transaction from: {}, to: {}, value: {}", sourceAddress, targetAddress, value);

            // Return the new transaction to the client
            return transaction;
        }

        @Override
        public void onSignedTransactionReceived(Transaction transaction) {
            // verification is in Internal.addNewTransaction
            addNewTransaction(transaction);
        }
    };
}