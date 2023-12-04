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
import org.jetbrains.annotations.TestOnly;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseBlockService {
    public static long CHECK_TRANSACTION_POOL_INTERVAL = 1_000; // ms
    public static int DEFAULT_DIFFICULTY = 8;
    protected final MiningService miningService = new MiningService();
    protected final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final Internal internal = new Internal();
    protected final Boolean isMiner;
    protected final String privateKey;
    protected final String publicKey;
    protected final String address;
    private final IStorage storage = Storage.getInstance();
    private final INetwork network = Network.getInstance();

    protected Callback callback;
    private final AtomicBoolean running = new AtomicBoolean(false);
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
    private final MiningService.Callback miningCallback = new MiningService.Callback() {
        @Override
        public void onNewBlockMined(Block block) {
            log.info("New block mined, height: {}, hash: {}", block.getHeight(), block.getHash());
            log.info("Nonce: {}", block.getNonce());
            log.info("Time used: {} ms", System.currentTimeMillis() - block.getTimestamp());
            addNewBlock(block);
            log.debug("Broadcasting new block");
            getNetwork().newBlock(block);
        }

        @Override
        public void onAllNonceTried(Block block) {
            log.error("How is this possible?");
            System.exit(-1);
            // Empty
            // Unlikely to use up all nonce
        }
    };
    protected Log log = Log.get(this);

    public BaseBlockService(boolean isMiner, String publicKey, String privateKey) {
        this.isMiner = isMiner;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.address = Hash.hashString(publicKey);
    }

    public void start() {
        log.info("Starting block service");

        running.set(true);

        // Remove transaction and utxo
        getStorage().cleanCache();

        Map<Long, Block> blockAll = getStorage().getBlockAll();
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
            miningService.setCallback(miningCallback);
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

    protected void addNewBlock(Block block) {
        executor.execute(() -> internal.addNewBlock(block));
    }

    final Set<String> receivedTransaction = new HashSet<>();

    protected void addNewTransaction(Transaction transaction) {
        if (!receivedTransaction.contains(transaction.hash)) {
            log.debug("Broadcasting new transaction");
            getNetwork().newTransaction(transaction);
            receivedTransaction.add(transaction.hash);
        } else {
            return;
        }
        log.info("New transaction received, hash: {}", transaction.hash);

        // verification is in Internal.addNewTransaction

        executor.execute(() -> internal.addNewTransaction(transaction));
    }

    private void scheduleCheckTransactionPool() {
        executor.schedule(checkTransactionPoolRunnable, CHECK_TRANSACTION_POOL_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private boolean checkTransactionPool() {
        long currentTime = new Date().getTime();

        Block lastBlock = getStorage().getLastBlock();
        if (lastBlock == null) {
            return true;
        }

        long previousBlockTime = lastBlock.getTimestamp();
        long timeDifference = currentTime - previousBlockTime;
        return timeDifference > 2_000 && !getStorage().getTransactionAll().isEmpty();
    }

    private List<Transaction> pollTransaction() {
        // Maybe just return all transactions
        return getStorage().getTransactionAll();
    }

    private int getDifficulty() {
        return DEFAULT_DIFFICULTY;
    }

    private Block generatePreMinedBlock(List<Transaction> transactions) {
        log.debug("Pre-generate block with {} transactions", transactions.size());

        Block previousBlock = getStorage().getLastBlock();

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
        block.setDifficulty(getDifficulty());
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
            if (!getStorage().hasUtxo(input)) {
                log.warn("Utxo {} is already in use", input);
                return false;
            }
        }
        return true;
    }

    private boolean checkSignature(Transaction transaction) {
        if (!transaction.verifyUser()) {
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
        getStorage().removeTransaction(transaction.hash);
        getStorage().addUtxoFromTransactionOutput(transaction);
        getStorage().removeUtxoFromTransactionInput(transaction);
    }

    protected IStorage getStorage() {
        return storage;
    }

    protected INetwork getNetwork() {
        return network;
    }

    abstract protected INetwork.Callback getNetworkCallback();

    @TestOnly
    public Transaction generateNewTransaction(String sourceAddress, String targetAddress, String value) {
        Set<TransactionInput> utxoList = getStorage().getUtxoByAddress(sourceAddress);
        List<TransactionInput> inputList = new ArrayList<>();
        List<TransactionOutput> outputList = new ArrayList<>();

        if (utxoList.isEmpty()) {
            TransactionInput input = new TransactionInput();
            input.address = "0".repeat(64);
            inputList.add(input);
        } else {
            inputList.add(utxoList.iterator().next());
        }

        TransactionOutput output1 = new TransactionOutput(targetAddress, value);
        output1.localChainId = Config.global.localChainId;
        outputList.add(output1);

        Transaction transaction = new Transaction(sourceAddress, targetAddress, inputList, outputList);

        log.info("Generate new transaction from: {}, data: {}", sourceAddress, value);

        return transaction;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     * Functions in this class in run on single thread to ensure thread-safety.<p>
     * Use <code>execute.schedule(new Runnable())</code> to run these functions.
     */
    private class Internal {

        private void addNewBlock(Block block) {
            if (!running.get()) {
                return;
            }

            if (block.getHeight() <= getStorage().getHeight()) {
                log.info("Block height {} is smaller than current height {}, skip.",
                        block.getHeight(), getStorage().getHeight());
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

            assert getStorage().getBlock(block.getPrevHash()) != null;
            getStorage().addBlock(block);
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
                log.warn("Unable to verify transaction {}", transaction.hash);
            }

            getStorage().addTransaction(transaction);
            getStorage().removeUtxoFromTransactionInput(transaction);
            getStorage().addPendingUtxoFromTransactionInput(transaction);

            log.info("New transaction added to pool, hash: {}", transaction.hash);
        }
    }

    public static class Callback {
        public String onGetUserDataLocation(String address) {
            return "";
        }

        public List<Transaction.Signature> onMoveUser(String address, String localChainId, List<Transaction.Signature> signatures) {
            return new ArrayList<>();
        }
    }
}
