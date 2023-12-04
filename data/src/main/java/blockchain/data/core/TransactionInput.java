package blockchain.data.core;

public class TransactionInput extends TransactionInputOutputBase {

    public String originalTxHash = "";
    public int originalOutputIndex = 0;

    public TransactionInput() {
        super("", 0);
    }

    public TransactionInput(String address, long value, String originalTxHash, int originalOutputIndex) {
        super(address, value);
        this.originalTxHash = originalTxHash;
        this.originalOutputIndex = originalOutputIndex;
    }
}