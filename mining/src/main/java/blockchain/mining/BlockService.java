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

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
        } else {
            scheduleCheckTransactionPool();
        }
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
            if (isValidTransaction(transaction)) {
                list.add(transaction);
            }
        }
        return list;
    }

    private boolean isValidTransaction(Transaction transaction) {
        // TODO: Implement validation logic
        // Check if inputs are valid

        // Check if outputs are valid
        double inputSum = transaction.getInputs().stream().mapToDouble(TransactionInput::getValue).sum();
        double outputSum = transaction.getOutputs().stream().mapToDouble(TransactionOutput::getValue).sum();
        if (inputSum < outputSum) {
            // The outputs exceed the inputs
            return false;
        }

        // The transaction is valid
        return true;
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
            // TODO: Check Transaction and add the transaction of new block into the pool
            // TODO: Remove UTXO used in this transaction
            if (isValidTransaction(transaction)) {
                storage.addTransaction(transaction);
            } else {
                log.error("Invalid transaction: " + transaction);
            }
        }

        private void revertBlock(String hash) {
            // TODO: Revert block, remove block data; add transaction back to the pool
            // Delete block data
            Block block = storage.getBlock(hash);
            storage.removeBlock(hash);

            // Add transactions back into the pool
            if (block != null) {
                List<Transaction> transactions = block.getData();
                for (Transaction transaction : transactions) {
                    storage.addTransaction(transaction);
                }

                // TODO: Blocks in previous height may also need to revert, remember to check them
            }
        }


    }

    private final MiningService.Callback callback = new MiningService.Callback() {
        @Override
        public void onNewBlockMined(Block block) {
            addNewBlock(block);
            //TODO: Broadcast the new block
            network.newBlock(block);
        }

        @Override
        public void onAllNonceTried(Block block) {
            // TODO: ues new timestamp then start mining again
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
            long balance = 0L;
            for (TransactionInput utxo : utxoList) {
                balance += utxo.getValue();
            }
            if (balance < value) {
                // Reject the transaction if the source address does not have enough funds
                throw new RuntimeException("Not enough balance in " + sourceAddress);
            }

            List<TransactionInput> userUtxoList = new ArrayList<>();
            // Generate a new transaction
            Transaction transaction = new Transaction();
            // TODO: get utxo list and generate input and output
//            transaction.sign();

            // Add the transaction to the transaction pool
            addNewTransaction(transaction);

            // Return the new transaction
            return transaction;
        }

        @Override
        public void onSignedTransactionReceived(Transaction transaction) {
            addNewTransaction(transaction);
        }
    };
}