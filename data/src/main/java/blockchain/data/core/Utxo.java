package blockchain.data.core;

public class Utxo extends TransactionOutput {
    // todo delete this class?

    public Utxo(String publicKey, long value) {
        super(publicKey, value);
    }
}