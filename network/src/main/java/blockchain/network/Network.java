package blockchain.network;

import blockchain.data.core.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Network {


    public static class Callback {
        void onNewBlockReceived(Block data) {

        }

        List<Block> onBlockWithHeightRequested(Long height) {
            return new ArrayList<>();
        }

        Map<Long, List<Block>> onBLockRangeRequested(Long heightMin, Long heightMax) {
            return new HashMap<>();
        }

        void onPeerJoined(String nodeId) {

        }

        void onPeerLost(String nodeId) {

        }
    }
}
