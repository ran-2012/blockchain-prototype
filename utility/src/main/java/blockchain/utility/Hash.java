package blockchain.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Hash {

    /**
     * 计算 SHA-256 哈希并返回十六进制结果
     * @param message 要计算哈希的数据
     * @return 哈希值
     * @throws NoSuchAlgorithmException 找不到 SHA-256 算法
     */
    public static String hash(String message) throws NoSuchAlgorithmException {
        MessageDigest msgDig = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = msgDig.digest(message.getBytes());
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

    /**
     * 计算 SHA-256 哈希值并计算结果的开头有多少个 0
     * @param message 要计算的数据
     * @return 一个 Map，包含两个元素，Hash 为哈希结果，NumOfZero 为开头的零的个数
     * @throws NoSuchAlgorithmException 找不到 SHA-256 算法
     */
    public static Map<String, Object> hashAndCount0(String message) throws NoSuchAlgorithmException {
        MessageDigest msgDig = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = msgDig.digest(message.getBytes());
        StringBuilder hexStr = new StringBuilder();
        boolean stillZero = true;
        int zeroNum = 0;
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexStr.append("0");
            }
            hexStr.append(hex);
            if (b == 0 && stillZero) {
                zeroNum++;
            } else {
                stillZero = false;
            }
        }
        Map<String, Object> result = new HashMap<>(2);
        result.put("Hash", hexStr.toString());
        result.put("NumOfZero", zeroNum);
        return result;
    }

}
