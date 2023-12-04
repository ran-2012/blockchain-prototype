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

    public static long CHECK_TRANSACTION_POOL_INTERVAL = 1_000; // ms

    public static int DEFAULT_DIFFICULTY = 8;

    private final IStorage storage = Storage.getInstance();
    private final INetwork network = Network.getInstance();

    private final MiningService miningService = MiningService.getInstance();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final Internal internal = new Internal();

    private final AtomicBoolean running = new AtomicBoolean(false);

    private final boolean isGlobalNode = false;

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

    private int getDifficulty() {
        return DEFAULT_DIFFICULTY;
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

            assert storage.getBlock(block.getPrevHash()) != null;
            storage.addBlock(block);
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

            storage.addTransaction(transaction);
            storage.removeUtxoFromTransactionInput(transaction);
            storage.addPendingUtxoFromTransactionInput(transaction);

            log.info("New transaction added to pool, hash: {}", transaction.hash);
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
        public Transaction onNewTransactionRequested(String sourceAddress, String targetAddress, String value) {
            return generateNewTransaction(sourceAddress, targetAddress, value);
        }

        final Set<String> receivedTransaction = new HashSet<>();

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

        @Override
        public void onGlobalNewBlockReceived(Block data) {
            super.onGlobalNewBlockReceived(data);
        }

        @Override
        public void onGlobalSignedTransactionReceived(Transaction transaction) {
            super.onGlobalSignedTransactionReceived(transaction);
        }

        @Override
        public String onGlobalGetUserLocation(String address) {
            return super.onGlobalGetUserLocation(address);
        }

        @Override
        public void onGlobalMoveUser(String address, String localChainId, List<Transaction.Signature> signatures) {
            super.onGlobalMoveUser(address, localChainId, signatures);
        }
    };

    @TestOnly
    public Transaction generateNewTransaction(String sourceAddress, String targetAddress, String value) {
        Set<TransactionInput> utxoList = storage.getUtxoByAddress(sourceAddress);
        List<TransactionInput> inputList = new ArrayList<>();
        List<TransactionOutput> outputList = new ArrayList<>();

        if (utxoList.isEmpty()) {

        }

        inputList.add(utxoList.iterator().next());

        TransactionOutput output1 = new TransactionOutput(targetAddress, value);
        outputList.add(output1);

        Transaction transaction = new Transaction(sourceAddress, targetAddress, inputList, outputList);

        log.info("Generate new transaction from: {}, data: {}", sourceAddress, value);

        return transaction;
    }
}