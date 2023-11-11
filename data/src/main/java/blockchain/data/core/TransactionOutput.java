package blockchain.data.core;

public class TransactionOutput extends TransactionInputOutputBase{

    public TransactionOutput(long publicKey, long value) {
        super(publicKey, value);
    }

    public TransactionOutput(long publicKey, String publicKeyHash, long value) { // todo implement publicKeyHash
        super(publicKey, publicKeyHash, value);
    }
}