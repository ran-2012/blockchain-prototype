package blockchain.data.core;

public class TransactionInput extends TransactionItem{

    public TransactionInput(String address, int value, String signature) {
        super(address, value, signature);
    }
}