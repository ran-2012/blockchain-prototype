package blockchain.wallet;

import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        CommandLine cl = new CommandLine(new Cli());

        cl.execute(args);
    }
}