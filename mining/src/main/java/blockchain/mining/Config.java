package blockchain.mining;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * Config for network, storage etc.
 */
public class Config {

    public static final String DEFAULT_PUBLIC_KEY = "30820122300d06092a864886f70d01010105000382010f003082010a0282010100e509b02f835afe01359daddca0790baee1dcc55a7888d76384fc6872ebcfed24f4b532b3c284ea0ad4ee97a74d4695fd91160b9853f4e3b0d9f88ce6bf838eafb66aee344ffced8bcf065ab373f1005dcab5154f5aa297567a7e3cfe04966f8f043829b27c0ee0bad0e0e023f29e4c202e6afa48361de765bf3cccce5526aef715915aa33a80291d7462ec7784eb0ba5acaf4815164a0260855afbf1a7bb3cf73fcd01bd9bce79452aa09a8578fe3d2d6e36759363169501bf66544d03695e3637e92232b75b55662b934d04306def7d46d4b3dd081b95c76103e0db3197947e02b83e08cb7310064f17af294f5ca85bc67f8778719d7ef938e0239d051a261d0203010001";
    @Parameter(names = {"--name"}, description = "Node name")
    public String name = "1";

    @Parameter(names = {"--mine"}, description = "Enable mining")
    public boolean isMiner = false;

    @Parameter(names = {"--port"}, description = "Local http port")
    public int port = 7070;

    @Parameter(names = "--peer", description = "Peers' HTTP url")
    public List<String> peers = new ArrayList<>();

    @Parameter(names = "--public-key", description = "Public key used in node")
    public String publicKey = "30820122300d06092a864886f70d01010105000382010f003082010a0282010100e509b02f835afe01359daddca0790baee1dcc55a7888d76384fc6872ebcfed24f4b532b3c284ea0ad4ee97a74d4695fd91160b9853f4e3b0d9f88ce6bf838eafb66aee344ffced8bcf065ab373f1005dcab5154f5aa297567a7e3cfe04966f8f043829b27c0ee0bad0e0e023f29e4c202e6afa48361de765bf3cccce5526aef715915aa33a80291d7462ec7784eb0ba5acaf4815164a0260855afbf1a7bb3cf73fcd01bd9bce79452aa09a8578fe3d2d6e36759363169501bf66544d03695e3637e92232b75b55662b934d04306def7d46d4b3dd081b95c76103e0db3197947e02b83e08cb7310064f17af294f5ca85bc67f8778719d7ef938e0239d051a261d0203010001";
}
