package blockchain.data.core;

public class Transaction {

    /**
     * Transaction hash
     */
    public String hash;

    /**
     * Address of transaction initiator
     */
    public String sourceAddress;

    public Transaction() {
        this.hash = "";
        this.sourceAddress = "";
    }

    public Transaction(String hash, String sourceAddress) {
        this.hash = hash;
        this.sourceAddress = sourceAddress;
    }
}
