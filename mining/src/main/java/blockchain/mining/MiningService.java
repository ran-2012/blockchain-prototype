package blockchain.mining;

import blockchain.data.core.Block;
import blockchain.data.exceptions.TXEmptyException;
import blockchain.utility.Log;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class MiningService {
    private final Log log = Log.get(this);

    public static MiningService instance = new MiningService();

    public static MiningService getInstance() {
        return instance;
    }

    private MiningService() {
    }

    Executor executor = Executors.newSingleThreadExecutor();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean mined = new AtomicBoolean(false);
    private final AtomicLong nonce = new AtomicLong(0);

    private Block block;
    private Callback callback;

    public boolean isRunning() {
        return running.get();
    }

    public Block getBlock() {
        return block;
    }

    public void start() {
        running.set(true);
        mined.set(false);
        nonce.set(0);

        executor.execute(() -> {
            while (running.get() && !mined.get()) {
                tryMine(nonce.get());
                nonce.incrementAndGet();
            }
        });
    }

    public void stop() {
        running.set(false);
    }

    private void tryMine(long nonce) {
        assert block != null;
        assert callback != null;

        try {
            if (block.mineBlock(nonce)) {
                mined.set(true);
                running.set(false);
                callback.onNewBlockMined(block);
            }
        } catch (TXEmptyException ignored) {
        }
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onNewBlockMined(Block block);

        default void onAllNonceTried(Block block) {

        }
    }
}
