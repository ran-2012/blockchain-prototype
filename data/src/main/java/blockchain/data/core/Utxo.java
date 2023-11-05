package blockchain.data.core;

public class Utxo extends TransactionOutput {
    public Utxo(String address, int value, String signature, boolean coinbase) {
        super(address, value, signature, coinbase);
    }
}