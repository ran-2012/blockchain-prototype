package blockchain.network;

import blockchain.data.core.BlockData;

public class Network {


    public static class Callback {
        void onNewBlockReceived(BlockData data) {

        }

        void onBlockWithIdRequested(Long blockId) {

        }

        void onBLockRangeRequested(Long min, Long max) {

        }

        void onPeerJoined(String nodeId) {

        }

        void onPeerLost(String nodeId) {

        }
    }
}
