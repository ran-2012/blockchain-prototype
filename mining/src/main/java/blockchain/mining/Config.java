package blockchain.mining;

import com.beust.jcommander.Parameter;

import java.util.List;

/**
 * Config for network, storage etc.
 */
public class Config {
    @Parameter(names = {"--name"}, required = true, description = "Node name")
    public String name;

    @Parameter(names = {"--mine"}, description = "Enable mining")
    public boolean isMiner = false;

    @Parameter(names = {"--port"}, required = true, description = "Local http port")
    public int port = 0;

    @Parameter(names = "--peer", description = "Peers' HTTP url")
    public List<String> peers;

    @Parameter(names = "--public-key", description = "Public key used in node")
    public String publicKey;
}
