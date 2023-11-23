package blockchain.network;

import blockchain.data.core.Block;
import blockchain.data.core.Transaction;

import java.util.HashMap;
import java.util.Map;

public interface INetwork {
    void registerCallback(Callback callback);

    void unregisterCallback(Callback callback);

    void newBlock(Block block);

    Map<String, Block> getBlock(String hash);

    Map<String, Map<Long, Block>> getBlockRange(Long heightMin, Long heightMax);

    class Callback {
        public void onNewBlockReceived(Block data) {

        }

        public Block onBlockWithHashRequested(String hash) {
            return null;
        }

        public Block onBlockWithHeightRequested(Long height) {
            return null;
        }

        public Map<Long, Block> onBlockRangeRequested(Long heightMin, Long heightMax) {
            return new HashMap<>();
        }

        public Transaction onNewTransactionRequested(String sourceAddress, String targetAddress, long value) {
            return null;
        }

        public void onSignedTransactionReceived(Transaction transaction) {

        }

        public void onPeerJoined(String nodeId) {

        }

        public void onPeerLost(String nodeId) {

        }
    }
}
