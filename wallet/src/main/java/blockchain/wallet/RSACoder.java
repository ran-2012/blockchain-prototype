package blockchain.wallet;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;




/**
 * RSA2工具
 *
 *
 */
public class RSACoder{
    //签名算法名称
    private static final String RSA_KEY_ALGORITHM = "RSA";

    //标准签名算法名称
    private static final String RSA_SIGNATURE_ALGORITHM = "SHA1withRSA";
    private static final String RSA2_SIGNATURE_ALGORITHM = "SHA256withRSA";

    public static Map<String,String> createKeyPair(int keySize){
        //为rsa算法创建keypairgenerator对象
        KeyPairGenerator kpg;
        try{
            kpg=KeyPairGenerator.getInstance("RSA");
        }catch (NoSuchAlgorithmException e){
            throw new IllegalArgumentException("算法不存在");
        };
        //初始化kpg对象，密钥长度
        kpg.initialize(keySize);
        //生成密钥对
        KeyPair keyPair=kpg.generateKeyPair();
        //生成公钥
        Key publicKey=keyPair.getPublic();
        String publickeyStr= Base64.encodeBase64URLSafeString(publicKey.getEncoded());

        //生成私钥
        Key privateKey=keyPair.getPrivate();
        String privatekeyStr= Base64.encodeBase64URLSafeString(privateKey.getEncoded());

        Map<String,String> keyPairMap=new HashMap<>();
        keyPairMap.put("publicKeyStr",publickeyStr);
        keyPairMap.put("privateKeyStr",privatekeyStr);

        return keyPairMap;
    }
   /* public static String sign(byte[] data, byte[] priKey, String signType) throws Exception {
        //创建PKCS8编码密钥规范
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(priKey);
        //返回转换指定算法的KeyFactory对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        //根据PKCS8编码密钥规范产生私钥对象
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        //标准签名算法名称(RSA还是RSA2)
        String algorithm = RSA_KEY_ALGORITHM.equals(signType) ? RSA_SIGNATURE_ALGORITHM : RSA2_SIGNATURE_ALGORITHM;
        //用指定算法产生签名对象Signature
        Signature signature = Signature.getInstance(algorithm);
        //用私钥初始化签名对象Signature
        signature.initSign(privateKey);
        //将待签名的数据传送给签名对象(须在初始化之后)
        signature.update(data);
        //返回签名结果字节数组
        byte[] sign = signature.sign();
        //返回Base64编码后的字符串
        return Base64.getEncoder().encodeToString(sign);
    }
    public static boolean verify(byte[] data, byte[] sign, byte[] pubKey, String signType) throws Exception {
        //返回转换指定算法的KeyFactory对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
        //创建X509编码密钥规范
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pubKey);
        //根据X509编码密钥规范产生公钥对象
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        //标准签名算法名称(RSA还是RSA2)
        String algorithm = RSA_KEY_ALGORITHM.equals(signType) ? RSA_SIGNATURE_ALGORITHM : RSA2_SIGNATURE_ALGORITHM;
        //用指定算法产生签名对象Signature
        Signature signature = Signature.getInstance(algorithm);
        //用公钥初始化签名对象,用于验证签名
        signature.initVerify(publicKey);
        //更新签名内容
        signature.update(data);
        //得到验证结果
        return signature.verify(sign);
    }*/

}



