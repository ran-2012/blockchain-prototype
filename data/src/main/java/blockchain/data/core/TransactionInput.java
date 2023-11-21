package blockchain.data.core;

public class TransactionInput extends TransactionInputOutputBase {

    public String originalTxHash = "";
    public int originalOutputIndex = 0;
    public String signature = "";
    public String publicKey = "";

    public TransactionInput() {
        super("", 0);
    }

    public TransactionInput(String address, long value, String originalTxHash, int originalOutputIndex, String publicKey, String signature) {
        super(address, value);
        this.publicKey = publicKey;
        this.originalTxHash = originalTxHash;
        this.originalOutputIndex = originalOutputIndex;
        this.signature = signature;
    }
}