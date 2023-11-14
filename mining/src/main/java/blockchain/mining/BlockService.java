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
import blockchain.network.INetwork;
import blockchain.network.Network;
import blockchain.storage.Storage;
import blockchain.utility.Hash;
import blockchain.utility.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class BlockService {

    Log log = Log.get(this);

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
     *         2.交易池的意思是我从里面选交易来挖矿，如果挖矿成功了也存储成功了，那么区块里包括的交易就需要从交易池里删除；相反，revert的block
     *         包括的交易也需要重新添加到交易池里面，是这样么？
     *         3.utxo好像没list
     *         4.******
     * @return
     */
    private boolean checkTransactionPool() {
        // TODO: Check the time between previous block and current time.
        long currentTime = new Date().getTime();
        long previousBlockTime = ((Storage) Storage.getInstance()).getLastBlock().getTimestamp();
        long timeDifference = currentTime - previousBlockTime;
        if (timeDifference > 60000) {
            return true;
        }
        return false;
    }

    private List<Transaction> poolTransaction() {
        ArrayList<Transaction> list = new ArrayList<Transaction>();
        //TODO: select valid transactions in the pool and add to the ArrayList list
        List<Transaction> pool = poolTransaction.getInstance().getTransactions();
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
        for (TransactionInput input : transaction.getInputs()) {
            TransactionOutput output = Storage.getInstance().getTransactionOutput(input.getTransactionOutputId());
            if (output == null) {
                // The referenced output does not exist
                return false;
            }
            if (input.getAmount() != output.getAmount()) {
                // The input amount is not equal to the original output amount
                return false;
            }
        }

        // Check if outputs are valid
        double inputSum = transaction.getInputs().stream().mapToDouble(TransactionInput::getAmount).sum();
        double outputSum = transaction.getOutputs().stream().mapToDouble(TransactionOutput::getAmount).sum();
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
        block.setPreviousHash(Storage.getInstance().getLastBlocks().get(0).getHash());
        block.setTimestamp(new Date().getTime());
        block.getTransactions().addAll(transactions);
        //TODO:set nonce
        block.setNonce(0);
        block.setHeight(Storage.getInstance().getLastBlocks().get(0).getHeight() + 1); // Set the block height to the previous block's height plus one
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
            // TODO: Check block; Add block to database, check if the blockchain has multi branches in the previous height
            // Check block
            if (!block.getHash().equals(block.calculateHash())) {
                log.error("Invalid block: hash mismatch");
                return;
            }
            Block lastBlock = Storage.getInstance().getLastBlock();
            if (!block.getPreviousHash().equals(lastBlock.getHash())) {
                log.error("Invalid block: previous hash mismatch");
                return;
            }

            // Add block to database
            Storage.getInstance().addBlock(block);
            // Check if the blockchain has multi branches in the previous height
            List<Block> lastBlocks = Storage.getInstance().getLastBlocks();
            if (lastBlocks.size() > 1) {
                // Remove other blocks and only maintain the block with the smallest hash value
                Block smallestBlock = lastBlocks.get(0);
                for (Block b : lastBlocks) {
                    if (b.getHash().compareTo(smallestBlock.getHash()) < 0) {
                        smallestBlock = b;
                    }
                }
                Storage.getInstance().removeBlocks(lastBlocks);
                Storage.getInstance().addBlock(smallestBlock);
            }
        }

        private void addNewTransaction(Transaction transaction) {
            //TODO:Check Transaction and add the transcation of new block into the pool
            if (isValidTransaction(transaction)) {
                Storage.getInstance().addTransaction(transaction);
            } else {
                log.error("Invalid transaction: " + transaction);
            }
        }

        private void revertBlock(String hash) {
            // TODO: Revert block, remove block data; add transaction and utxo back to the pool
            // Delete block data
            Storage.getInstance().removeBlockByHash(hash);

            // Add transactions back into the pool
            Block block = Storage.getInstance().getBlock(hash);
            if (block != null) {
                List<Transaction> transactions = block.getTransactions();
                for (Transaction transaction : transactions) {
                    Storage.getInstance().addTransaction(transaction);
                }

                // TODO: Blocks in previous height may also need to revert, remember to check them
            }
        }

        private final MiningService.Callback callback = new MiningService.Callback() {
            @Override
            public void onNewBlockMined(Block block) {
                addNewBlock(block);
                //TODO: Broadcast the new block
                INetwork network = Network.getInstance();
                network.newBlock(block);
            }
        };
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
        public Transaction onNewTransactionRequested(String sourceAddress, String targetAddress, Long value) {
            // TODO: query utxo list and generate a transaction (or reject this transaction)
            List<Utxo> utxoList = Storage.getInstance().getUTXOList(sourceAddress);
            Long balance = 0L;
            for (Utxo utxo : utxoList) {
                balance += utxo.getValue();
            }
            if (balance < value) {
                // Reject the transaction if the source address does not have enough funds
                return null;
            }

            // Generate a new transaction
            Transaction transaction = new Transaction(sourceAddress, targetAddress, value);
            transaction.sign();

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