package blockchain.data.core;

public class TransactionOutput extends TransactionInputOutputBase {

    public TransactionOutput() {
        super("", 0);
    }

    public TransactionOutput(String address, long value) {
        super(address, value);
    }

}