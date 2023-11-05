package blockchain.data.core;

public class TransactionInput extends TransactionInputOutputBase{

    public TransactionInput(String address, int value, String signature) {
        super(address, value, signature);
    }
}