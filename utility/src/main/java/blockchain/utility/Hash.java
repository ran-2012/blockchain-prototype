package blockchain.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Hash {

    /**
     * 计算 SHA-256 哈希并返回十六进制结果
     *
     * @param message 要计算哈希的数据
     * @return 哈希值
     * @throws NoSuchAlgorithmException 找不到 SHA-256 算法
     */
    public static byte[] hash(String message) throws NoSuchAlgorithmException {
        MessageDigest msgDig = MessageDigest.getInstance("SHA-256");
        return msgDig.digest(message.getBytes());
    }

    public static String hashString(String message) throws NoSuchAlgorithmException {
        return byteToString(hash(message));
    }

    public static String byteToString(byte[] hashBytes) {
        StringBuilder hexStr = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexStr.append("0");
            }
            hexStr.append(hex);
        }
        return hexStr.toString();
    }

    public static int countZero(byte[] hashBytes) {
        int zeroNum = 0;
        for (byte b : hashBytes) {
            int i = b & 0x80;
            int limit = 8;
            while (i == 0 && limit > 0) {
                zeroNum += 1;
                b = (byte) (b << 1);
                i = b & 0x80;
                limit--;
            }
            if (i != 0) {
                break;
            }
        }
        return zeroNum;
    }

    /**
     * 计算 SHA-256 哈希值并计算结果的开头有多少个 0
     *
     * @param message 要计算的数据
     * @return 一个 Map，包含两个元素，Hash 为哈希结果，NumOfZero 为开头的零的个数
     * @throws NoSuchAlgorithmException 找不到 SHA-256 算法
     */
    public static Map<String, Object> hashAndCount0(String message) throws NoSuchAlgorithmException {
        byte[] hashBytes = hash(message);
        Map<String, Object> result = new HashMap<>(2);
        result.put("Hash", byteToString(hashBytes));
        result.put("NumOfZero", countZero(hashBytes));
        return result;
    }

}
