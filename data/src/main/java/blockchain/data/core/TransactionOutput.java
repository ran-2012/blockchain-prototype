package blockchain.data.core;

public class TransactionOutput extends TransactionInputOutputBase {

    public String dataHash = "";

    public String localChainId = "";

    public TransactionOutput() {
        super("", 0);
    }

    public TransactionOutput(String address, String dataHash) {
        super(address, 0);
    }

}