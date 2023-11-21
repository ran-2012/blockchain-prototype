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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("FieldCanBeLocal")
public class BlockService {

    Log log = Log.get(this);


    public static long CHECK_TRANSACTION_POOL_INTERVAL = 1000;

    public static long TARGET_TIME = 10_000;

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

    private void revertBlock(String hash) {
        executor.execute(() -> internal.revertBlock(hash));
    }

    private void addNewTransaction(Transaction transaction) {
        executor.execute(() -> internal.addNewTransaction(transaction));
    }

    private void scheduleCheckTransactionPool() {
        executor.schedule(checkTransactionPoolRunnable, CHECK_TRANSACTION_POOL_INTERVAL, TimeUnit.MILLISECONDS);
    }

    /**
     * 现有问题：1.需要一个能获取当前block的函数，不是正在挖的，而是链上最后一个高度上面的block
     * 2.交易池的意思是我从里面选交易来挖矿，如果挖矿成功了也存储成功了，那么区块里包括的交易就需要从交易池里删除；相反，revert的block
     * 包括的交易也需要重新添加到交易池里面，是这样么？
     * 3.utxo好像没list
     * 4.******
     *
     * @return
     */
    private boolean checkTransactionPool() {
        // TODO: Check the time between previous block and current time.
        long currentTime = new Date().getTime();
        Block lastBlock = storage.getLastBlock();
        long previousBlockTime = lastBlock.getTimestamp();
        long timeDifference = currentTime - previousBlockTime;
        return timeDifference > 60000;
    }

    private List<Transaction> pollTransaction() {
        ArrayList<Transaction> list = new ArrayList<>();
        //TODO: select valid transactions in the pool and add to the ArrayList list
        List<Transaction> pool = new ArrayList<>();


        for (Transaction transaction : pool) {
            // No check here, all transaction is checked before adding to pool
//            list.add(transaction);
        }
        return list;
    }

    private Block generateNewBlock(List<Transaction> transactions) {
        // Generate block here then mine
        Block block = new Block();
        //TODO:Fill related data into the new block
        block.setPrevHash(storage.getLastBlock().getHash());
        block.setTimestamp(new Date().getTime());
        for (Transaction transaction : transactions) {
            try {
                block.addTransaction(transaction);
            } catch (AlreadyMinedException ignored) {
            }
        }
        //TODO:set nonce
        block.setNonce(0);
        block.setHeight(storage.getLastBlock().getHeight() + 1); // Set the block height to the previous block's height plus one
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

            Block lastBlock = storage.getLastBlock();
            if (!block.getPrevHash().equals(lastBlock.getHash())) {
                log.error("Invalid block: previous hash mismatch");
                return;
            }

            // Add block to database
            storage.addBlock(block);
            storage.setHeight(block.getHeight());
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

        private void revertBlock(String hash) {
            // Delete block data
            Block block = storage.getBlock(hash);
            storage.removeBlock(hash);

            // Add transactions back into the pool
            if (block != null) {
                List<Transaction> transactions = block.getData();
                for (Transaction transaction : transactions) {
                    storage.addPendingUtxoFromTransactionInput(transaction);
                    storage.addTransaction(transaction);
                }
                // TODO: Check all utxo in the reverted block and remove them and transactions use these utxo
            }
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