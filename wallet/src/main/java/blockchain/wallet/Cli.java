package blockchain.wallet;

import blockchain.data.core.Transaction;
import blockchain.data.core.Utxo;
import blockchain.network.client.HttpClient;
import blockchain.utility.Rsa;
import com.google.gson.Gson;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Command(name = "wallet")
public class Cli {

    public static String CONFIG_NAME = "config.json";

    private final Gson gson = new Gson();

    private Config config = new Config();
    private HttpClientWrapper client;

    public Cli() {
        loadConfig();
        client = new HttpClientWrapper(config.nodeUrl);
    }

    private void loadConfig() {
        try {
            File file = new File(CONFIG_NAME);
            file.createNewFile();
            FileReader fileReader = new FileReader(file);
            this.config = gson.fromJson(fileReader, Config.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void saveConfig() {
        try {
            File file = new File(CONFIG_NAME);
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(gson.toJson(config));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Command(name = "generate", description = {"Generate a new pair of keys"})
    public int generate() {
        Map<String, String> map = Rsa.generateKey();
        Config.KeyPair pair = new Config.KeyPair();
        pair.pk = map.get("pk");
        pair.sk = map.get("sk");
        config.list.add(pair);
        saveConfig();
        return 0;
    }

    @Command(name = "list", description = {"List addresses"})
    public int list() {
        for (int i = 0; i < config.list.size(); ++i) {
            Config.KeyPair pair = config.list.get(i);
            System.out.printf("%d. %s", i + 1, pair.pk.substring(0, 19));
        }
        return 0;
    }

    @Command(name = "add", description = {"Add existing key"})
    public int add(@Parameters(paramLabel = "SECRET KEY") String secretKey,
                   @Parameters(paramLabel = "PUBLIC KEY") String publicKey) {
        Config.KeyPair pair = new Config.KeyPair();
        pair.sk = secretKey;
        pair.pk = publicKey;
        config.list.add(pair);
        saveConfig();
        return 0;
    }

    @Command(name = "delete", description = "Delete key")
    public int delete(@Parameters(paramLabel = "ADDRESS INDEX") int index) {
        if (index > config.list.size() + 1) {
            throw new IllegalArgumentException("Index must be in valid range");
        }
        config.list.remove(index);
        return 0;
    }

    @Command(name = "transfer", description = "Transfer money to target address")
    public int transfer(@Parameters(paramLabel = "SOURCE INDEX") int addressIndex,
                        @Parameters(paramLabel = "TARGET") String targetAddress,
                        @Parameters(paramLabel = "VALUE") long value) {
        String sourceAddress = config.list.get(addressIndex).pk;
        try {
            Transaction transaction = client.getTransaction(sourceAddress, targetAddress, value);
            // TODO: Sign transaction;
            client.postSignedTransaction(transaction);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Command(name = "balance", description = {"Query balance"})
    public int balance(@Parameters(paramLabel = "ADDRESS INDEX") int addressIndex) {
        String sourceAddress = config.list.get(addressIndex).pk;
        try {
            List<Utxo> list = client.getUtxoList(sourceAddress);
            long balance = 0;
            for (Utxo utxo : list) {

                balance += utxo.getValue();
            }
            System.out.println(balance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
