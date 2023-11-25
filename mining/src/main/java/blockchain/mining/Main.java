package blockchain.mining;

import blockchain.network.Network;
import blockchain.storage.Storage;
import blockchain.utility.Log;
import com.beust.jcommander.JCommander;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        Log log = Log.get("Mining");

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

        BlockService service = new BlockService(config.isMiner, config.publicKey);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.warn("Exiting, node: {}", config.name);
            service.stop();
        }));

        while (true) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
