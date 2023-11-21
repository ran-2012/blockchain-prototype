package blockchain.utility;

import com.google.gson.Gson;

public class Json {
    private static Gson gson = new Gson();

    public static String toJson(Object o) {
        return gson.toJson(o);
    }

    public static <T> T fromJson(String str, Class<T> clz) {
        return gson.fromJson(str, clz);
    }
}
