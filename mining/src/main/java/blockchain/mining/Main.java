package blockchain.mining;

import blockchain.network.Network;
import blockchain.storage.Storage;
import blockchain.utility.Log;
import com.beust.jcommander.JCommander;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        Config config = new Config();
        JCommander.newBuilder()
                .addObject(config)
                .build()
                .parse(args);

        Log.setTag(config.name);

        Log log = Log.get("Mining");
        log.info("Starting Node: {}, at port: {}, enable mining: {}", config.name, config.port, config.isMiner);

        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            log.error("Unhandled exception in thread: {}", t.getName());
            log.error(e);
        });

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

        service.start();

        while (true) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
