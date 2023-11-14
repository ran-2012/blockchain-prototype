package blockchain.data.core;

public class TransactionOutput extends TransactionInputOutputBase{

    public TransactionOutput(String publicKey, long value) {
        super(publicKey, value);
    }

    public TransactionOutput(String publicKey, String publicKeyHash, long value) { // todo implement publicKeyHash
        super(publicKey, publicKeyHash, value);
    }
}