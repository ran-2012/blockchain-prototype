package blockchain.utility;

import org.apache.commons.codec.DecoderException;

public class Hex {
    public static String toString(byte[] bytes){
        return org.apache.commons.codec.binary.Hex.encodeHexString(bytes);
    }

    public static byte[] toBytes(String string){
        try {
            return org.apache.commons.codec.binary.Hex.decodeHex(string);
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }
    }
}
