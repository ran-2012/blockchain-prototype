package blockchain.utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

public class HashTest {

    @Test
    void hash() {
    }

    @Test
    void hashAndCount0() {


    }

    @Test
    public void countZero() {
        byte[] bytes = {0x01, (byte) 0xff, 0x01};
        int result = Hash.countZero(bytes);

        Assertions.assertEquals(7, result);
    }

    @Test
    void hexTest() {
        String testStr = "123a98703a3f";
        byte[] bytes = Hex.toBytes(testStr);
        Assertions.assertEquals(testStr, Hex.toString(bytes));
    }
}