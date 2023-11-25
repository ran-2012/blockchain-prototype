package blockchain.data.core;

import blockchain.utility.Hash;
import blockchain.utility.Json;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;


public class Transaction {

    public String hash;

    public List<TransactionInput> inputs = new ArrayList<>();
    public List<TransactionOutput> outputs = new ArrayList<>();

    public String outputHash = "";
    public String sourcePublicKey = "";
    public String outputSignature = "";

    public long fee;
    public long timestamp;

    public boolean coinbase;

    public String sourceAddress = "";
    public String targetAddress = "";

    public Transaction(TransactionOutput coinbaseOutput) {
        this.inputs = null;
        this.outputs = new ArrayList<>(1);
        outputs.add(coinbaseOutput);
        this.targetAddress = coinbaseOutput.address;
        this.timestamp = System.currentTimeMillis();
        this.coinbase = true;
        this.fee = 0;
        this.hash = updateHash();
    }

    public Transaction(String sourceAddress, String targetAddress, List<TransactionInput> txInputs, List<TransactionOutput> txOutputs) {
        this.inputs = txInputs;
        this.outputs = txOutputs;
        this.timestamp = System.currentTimeMillis();
        this.coinbase = false;
        this.outputHash = Hash.hashString(this.outputs);
        this.fee = calculateFee();
        this.hash = updateHash();
    }

    public Transaction() {
        this.timestamp = System.currentTimeMillis();
        this.hash = updateHash();
    }

    @TestOnly
    public Transaction(String hash) {
        this.hash = hash;
        this.timestamp = System.currentTimeMillis();
        this.hash = updateHash();
    }

    public String toString() {
        return Json.toJson(this);
    }

    public String updateHash() {
        hash = "";
        hash = Hash.hashString(this);
        return hash;
    }

    public long calculateFee() {
        long fee = 0;
        for (TransactionInput txIn : inputs) {
            fee = fee + txIn.getValue();
        }
        for (TransactionOutput txOut : outputs) {
            fee = fee - txOut.getValue();
        }
        return fee;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public List<TransactionInput> getInputs() {
        return inputs;
    }

    public void setInputs(List<TransactionInput> inputs) {
        this.inputs = inputs;
    }

    public List<TransactionOutput> getOutputs() {
        return outputs;
    }

    public void setOutputs(ArrayList<TransactionOutput> outputs) {
        this.outputs = outputs;
    }

    public long getFee() {
        return fee;
    }

    public void setFee(long fee) {
        this.fee = fee;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isCoinbase() {
        return coinbase;
    }

    public void setCoinbase(boolean coinbase) {
        this.coinbase = coinbase;
    }
}
