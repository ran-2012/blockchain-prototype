package blockchain.wallet;

import blockchain.data.core.Transaction;
import blockchain.data.core.TransactionInput;
import blockchain.data.core.TransactionOutput;
import blockchain.utility.Hash;
import blockchain.utility.Log;
import blockchain.utility.Rsa;
import com.google.gson.Gson;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.*;
import java.util.List;
import java.util.Map;

@Command(name = "wallet")
public class Cli {

    private final Log log = Log.get(this);

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
        Config.Pair pair = new Config.Pair();
        pair.pk = map.get("pk");
        pair.sk = map.get("sk");
        pair.address = Hash.hashString(pair.pk);
        config.list.add(pair);
        saveConfig();
        return 0;
    }

    @Command(name = "list", description = {"List addresses"})
    public int list() {
        for (int i = 0; i < config.list.size(); ++i) {
            Config.Pair pair = config.list.get(i);
            System.out.printf("%d. %s", i + 1, pair.pk.substring(0, 19));
        }
        return 0;
    }

    @Command(name = "add", description = {"Add existing key"})
    public int add(@Parameters(paramLabel = "SECRET KEY") String secretKey,
                   @Parameters(paramLabel = "PUBLIC KEY") String publicKey) {
        Config.Pair pair = new Config.Pair();
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

        Config.Pair pair = config.list.get(addressIndex);
        String sourceAddress = pair.address;
        try {
            Transaction transaction = client.getTransaction(sourceAddress, targetAddress, value);
            if (checkTransaction(sourceAddress, targetAddress, value, transaction)) {
                signTransaction(sourceAddress, pair.pk, pair.sk, transaction);
            }
            transaction.updateHash();
            client.postSignedTransaction(transaction);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
        return 0;
    }

    private boolean checkTransaction(String sourceAddress, String targetAddress, long value, Transaction transaction) {
        long inputValue = 0;
        long outputValue = 0;
        for (TransactionInput input : transaction.inputs) {
            if (input.address.equals(sourceAddress)) {
                inputValue += input.value;
            }
        }
        for (TransactionOutput output : transaction.outputs) {
            if (output.address.equals(sourceAddress)) {
                inputValue -= output.value;
            } else if (output.address.equals(targetAddress)) {
                outputValue += output.value;
            } else {
                log.error("Output address not match");
                return false;
            }
        }
        if (inputValue > value * 1.1) {
            log.error("Input value not match");
            return false;
        }
        if (outputValue != value) {
            log.error("Output value not match");
            return false;
        }
        return true;
    }

    private void signTransaction(String address, String publicKey, String privateKey, Transaction transaction) {
        for (TransactionInput input : transaction.inputs) {
            if (input.address.equals(address)) {
                input.publicKey = publicKey;
                input.signature = "";
                input.signature = Rsa.sign(input, privateKey);
            }
        }
        transaction.outputSignature = Rsa.sign(transaction.outputHash, privateKey);
    }

    @Command(name = "balance", description = {"Query balance"})
    public int balance(@Parameters(paramLabel = "ADDRESS INDEX") int addressIndex) {
        String sourceAddress = config.list.get(addressIndex).pk;
        try {
            List<TransactionInput> list = client.getUtxoList(sourceAddress);
            long balance = 0;
            for (TransactionInput utxo : list) {

                balance += utxo.getValue();
            }
            System.out.println(balance);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
        return 0;
    }
}
