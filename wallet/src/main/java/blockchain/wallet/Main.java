package blockchain.wallet;

import blockchain.utility.Log;
import picocli.CommandLine;

public class Main {
    public static void main(String[] args) {
        Log.setTag("wallet");
        CommandLine cl = new CommandLine(new Cli());

        cl.execute(args);
    }
}