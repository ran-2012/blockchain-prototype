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
import blockchain.storage.Storage;
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
    private boolean checkTransactionPool() {
        // TODO: Check number of transaction in pool or time between previous block and current time.
        return false;
    }

    private List<Transaction> pollTransaction() {
        ArrayList list = new ArrayList<Transaction>();
        // TODO: select transactions in the pool
        return list;
    }

    private Block generateNewBlock(List<Transaction> transactions) {
        // Generate block here then mine
        Block block = new Block();
        // TODO: Fill data
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
            // TODO: Check block; Add block to database, check if has multi branches
        }

        private void addNewTransaction(Transaction transaction) {
            // TODO: Check Transaction; Add to pool
        }

        private void revertBlock(String hash) {
            // TODO: Revert block, remove block data; add transaction and utxo back to the pool
            // TODO: Blocks in previous height may also need to revert, remember to check them
        }
    }

    private final MiningService.Callback callback = new MiningService.Callback() {
        @Override
        public void onNewBlockMined(Block block) {
            addNewBlock(block);
            // TODO: Broadcast the block
        }

        @Override
        public void onAllNonceTried(Block block) {
            // TODO: ues new timestamp then start again
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
            return super.onNewTransactionRequested(sourceAddress, targetAddress, value);
        }

        @Override
        public void onSignedTransactionReceived(Transaction transaction) {
            addNewTransaction(transaction);
        }
    };
}