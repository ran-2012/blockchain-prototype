package blockchain.data.core;

public class TransactionOutput extends TransactionItem{

    boolean coinbase = false;

    public TransactionOutput(String address, int value, String signature, boolean coinbase) {
        super(address, value, signature);
        this.coinbase = coinbase;
    }

    public boolean isCoinbase() {
        return coinbase;
    }

    public void setCoinbase(boolean coinbase) {
        this.coinbase = coinbase;
    }
}