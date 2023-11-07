package blockchain.network;

import blockchain.data.core.Block;
import blockchain.data.core.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Network {


    public void registerCallback(Callback callback) {

    }

    public void unregisterCallback(Callback callback) {

    }

    public static class Callback {
        public void onNewBlockReceived(Block data) {

        }

        public Block onBlockWithHashRequested(String hash) {
            return null;
        }

        public List<Block> onBlockWithHeightRequested(Long height) {
            return new ArrayList<>();
        }

        public Map<Long, List<Block>> onBLockRangeRequested(Long heightMin, Long heightMax) {
            return new HashMap<>();
        }

        public Transaction onNewTransactionRequested(String sourceAddress, String targetAddress, Long value) {
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
