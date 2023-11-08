package blockchain.network;

import java.util.HashMap;

public class Network {

    private static INetwork instance;

    public static void init(int port, HashMap<String, String> initialPeerMap) {
        instance = new NetworkInternal(port, initialPeerMap);
    }

    public static INetwork getInstance() {
        if (instance == null) {
            throw new RuntimeException("Network instance not initialized");
        }
        return instance;
    }

    private Network() {
    }
}
