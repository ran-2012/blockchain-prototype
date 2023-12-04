package blockchain.data.core;

public class TransactionOutput extends TransactionInputOutputBase {

    public String data = "";

    public String localChainId = "";

    public TransactionOutput() {
        super("", 0);
    }

    public TransactionOutput(String address, String data) {
        super(address, 0);
        this.data = data;
    }

}