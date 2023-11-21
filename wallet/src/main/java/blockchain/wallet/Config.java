package blockchain.wallet;

import java.util.ArrayList;
import java.util.List;

public class Config {

    public List<Pair> list = new ArrayList<>();

    public String nodeUrl = "http://localhost:7070";

    public static class Pair {
        String pk;
        String sk;
        String address;
    }
}
