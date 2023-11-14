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
import blockchain.data.core.Utxo;
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
        executor.schedule(checkTransactionPoolRunnable, 1, TimeUnit.SECONDS);
    }

    /**
     * Check if it has enough transaction to start mining.
     *
     * @return True: You can start mining now
     */


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
        List<Block> lastBLocks = storage.getLastBlock();
        Block lastBlock = lastBLocks.get(0);
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
        block.setPrevHash(storage.getLastBlock().get(0).getHash());
        block.setTimestamp(new Date().getTime());
        for (Transaction transaction : transactions) {
            try {
                block.addTransaction(transaction);
            } catch (AlreadyMinedException ignored) {
            }
        }
        //TODO:set nonce
        block.setNonce(0);
        block.setHeight(storage.getLastBlock().get(0).getHeight() + 1); // Set the block height to the previous block's height plus one
        try {
            block.setHash(Hash.hashString(block.toString()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
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
            // TODO: Check block; Add block to database, check if the blockchain has multi branches in the previous height
            // Check block
            try {
                block.validate();
            } catch (Exception e) {
                log.error("Invalid block: hash mismatch");
                return;
            }
            List<Block> lastBlocks = storage.getLastBlock();
            Block lastBlock = lastBlocks.get(0);
            if (!block.getPrevHash().equals(lastBlock.getHash())) {
                log.error("Invalid block: previous hash mismatch");
                return;
            }

            // Add block to database
            storage.addBlock(block);
            // Check if the blockchain has multi branches in the previous height
            lastBlocks = storage.getLastBlock();
            if (lastBlocks.size() > 1) {
                // Remove other blocks and only maintain the block with the smallest hash value
                Block smallestBlock = lastBlocks.get(0);
                for (Block b : lastBlocks) {
                    if (b.getHash().compareTo(smallestBlock.getHash()) < 0) {
                        smallestBlock = b;
                    }
                }
                for (Block b : lastBlocks) {
                    revertBlock(b.getHash());
                }
                if (storage.getBlock(smallestBlock.getHash()) == null) {
                    storage.addBlock(smallestBlock);
                }
            }
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
            storage.removeBlockByHash(hash);

            // Add transactions back into the pool
            Block block = storage.getBlock(hash);
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
            // TODO: query utxo list and generate a transaction (or reject this transaction)
            Set<Utxo> utxoList = storage.getUtxoByAddress(sourceAddress);
            long balance = 0L;
            for (Utxo utxo : utxoList) {
                balance += utxo.getValue();
            }
            if (balance < value) {
                // Reject the transaction if the source address does not have enough funds
                return null;
            }

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