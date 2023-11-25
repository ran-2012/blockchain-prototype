package blockchain.wallet;

import blockchain.utility.Hash;
import blockchain.utility.Rsa;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Config {

    private static final Gson gson = new Gson();

    public static final String CONFIG_NAME = "config.json";

    public static Config load() {
        try {
            File file = new File(CONFIG_NAME);
            if (file.createNewFile()) {
                return new Config();
            }
            FileReader fileReader = new FileReader(file);
            return gson.fromJson(fileReader, Config.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static void save(Config config) {
        try {
            File file = new File(CONFIG_NAME);
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(gson.toJson(config));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Pair generateNewPair() {
        Map<String, String> map = Rsa.generateKey();
        Config.Pair pair = new Config.Pair();
        pair.pk = map.get("pk");
        pair.sk = map.get("sk");
        pair.address = Hash.hashString(pair.pk);
        return pair;
    }

    public List<Pair> list = new ArrayList<>();

    public String nodeUrl = "http://localhost:7070";

    public static class Pair {
        public String pk;
        public String sk;
        public String address;
    }
}
