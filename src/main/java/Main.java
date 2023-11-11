import blockchain.network.Network;
import blockchain.storage.Storage;
import blockchain.utility.Log;
import com.beust.jcommander.JCommander;

import java.util.HashMap;

public class Main {
    private static Log log = Log.get(Main.class);

    public static void init() {

    }

    public static void main(String[] args) {
        Config config = new Config();
        JCommander.newBuilder()
                .addObject(config)
                .build()
                .parse(args);

        log.info("Starting Node: {}, at port: {}, enable mining: {}", config.name, config.port, config.isMiner);
        Storage.initialize(config.name);
        HashMap<String, String> peerMap = new HashMap<>();
        for (int i = 0; i < config.peers.size(); ++i) {
            peerMap.put(String.valueOf(i), config.peers.get(i));
        }
        Network.init(config.port, peerMap);
    }
}
