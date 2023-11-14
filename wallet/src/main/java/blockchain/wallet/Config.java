package blockchain.wallet;

import java.util.ArrayList;
import java.util.List;

public class Config {

    public List<KeyPair> list = new ArrayList<>();

    public String nodeUrl = "http://localhost:7070";

    public static class KeyPair {
        String pk;
        String sk;
    }
}
