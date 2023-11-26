package blockchain.utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class RsaTest {
    @Test
    void signTest() {
        Map<String, String> pair = Rsa.generateKey();
        String pk = pair.get("pk");
        String sk = pair.get("sk");

        String data = "123123123";
        String signature = Rsa.sign(data, sk);
        Assertions.assertTrue(Rsa.verify(data, signature, pk));
    }
}
