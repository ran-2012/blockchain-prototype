package blockchain.data.core;

public class TransactionInput extends TransactionInputOutputBase{

    String originalTxId;
    int originalOutputIndex;
    String signature;

    public TransactionInput(String originalTxId, int originalOutputIndex, String signature) {
        super(17, 100);  // todo need to get original transaction information from the storage nodes
        this.originalTxId = originalTxId;
        this.originalOutputIndex = originalOutputIndex;
        this.signature = signature;
    }

    public TransactionInput(String originalTxId, int originalOutputIndex, long publicKey, long value, String signature) {
        super(publicKey, value);  // todo delete this
        this.originalTxId = originalTxId;
        this.originalOutputIndex = originalOutputIndex;
        this.signature = signature;
    }

    public TransactionInput(String originalTxId, int originalOutputIndex, long publicKey, String publicKeyHash, long value, String signature) {
        super(publicKey, publicKeyHash, value);  // todo delete
        this.originalTxId = originalTxId;
        this.originalOutputIndex = originalOutputIndex;
        this.signature = signature;
    }

    @Override
    public String contentString(){
        return "originalTxId=".concat(originalTxId).concat(",originalOutputIndex=").
                concat(Integer.toString(originalOutputIndex)).concat("publicKey=").
                concat(Long.toString(publicKey)).concat(",value=").concat(Long.toString(value));  // todo add publicKeyHash
    }
}