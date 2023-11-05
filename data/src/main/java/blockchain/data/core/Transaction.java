package blockchain.data.core;

import java.util.ArrayList;


public class Transaction {

    /**
     * Transaction hash
     */
    public String hash;

    ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    /**
     * Address of transaction initiator
     */
    public String sourceAddress;  // todo test

    public Transaction() {
        this.hash = "";
        this.sourceAddress = "";  // todo test
    }

    public Transaction(String hash, String sourceAddress) {
        this.hash = hash;
        this.sourceAddress = sourceAddress; // todo test
    }

    public Transaction(String hash, ArrayList<TransactionInput> inputs, ArrayList<TransactionOutput> outputs) {
        this.hash = hash;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public String getHash() {
        return hash;
    }

    public ArrayList<TransactionInput> getInputs() {
        return inputs;
    }

    public ArrayList<TransactionOutput> getOutputs() {
        return outputs;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setInputs(ArrayList<TransactionInput> inputs) {
        this.inputs = inputs;
    }

    public void setOutputs(ArrayList<TransactionOutput> outputs) {
        this.outputs = outputs;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }
}
