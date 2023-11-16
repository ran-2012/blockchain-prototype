package blockchain.data.core;

public class TransactionInput extends TransactionInputOutputBase {

    public String originalTxId;
    public int originalOutputIndex;
    public String signature;

    public TransactionInput(String originalTxId, int originalOutputIndex, String publicKey, long value, String signature) {
        super(publicKey, value);  // todo delete this
        this.originalTxId = originalTxId;
        this.originalOutputIndex = originalOutputIndex;
        this.signature = signature;
    }

    public TransactionInput(String originalTxId, int originalOutputIndex, String publicKey, String publicKeyHash, long value, String signature) {
        super(publicKey, publicKeyHash, value);  // todo delete
        this.originalTxId = originalTxId;
        this.originalOutputIndex = originalOutputIndex;
        this.signature = signature;
    }

    @Override
    public String contentString() {
        return "originalTxId=".concat(originalTxId).concat(",originalOutputIndex=").
                concat(Integer.toString(originalOutputIndex)).concat("publicKey=").
                concat(publicKey).concat(",value=").concat(Long.toString(value));  // todo add publicKeyHash
    }
}