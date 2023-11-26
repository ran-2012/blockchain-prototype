package blockchain.mining;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Config for network, storage etc.
 */
public class Config {
    @Parameter(names = {"--name"}, description = "Node name")
    public String name = "0";

    @Parameter(names = {"--mine"}, description = "Enable mining")
    public boolean isMiner = false;

    @Parameter(names = {"--port"}, description = "Local http port")
    public int port = 7070;

    @Parameter(names = "--peer", description = "Peers' HTTP url")
    public List<String> peers = new ArrayList<>();

    @Parameter(names = "--public-key", description = "Public key used in node")
    public String publicKey = "";
}
