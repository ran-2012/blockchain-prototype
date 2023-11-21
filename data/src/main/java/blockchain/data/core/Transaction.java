package blockchain.data.core;

import blockchain.utility.Hash;
import blockchain.utility.Json;
import org.jetbrains.annotations.TestOnly;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;


public class Transaction {

    public String hash;

    public ArrayList<TransactionInput> inputs = new ArrayList<>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<>();

    public String outputHash = "";
    public String outputSignature = "";
    public long fee;
    public long timestamp;

    public boolean coinbase;

    /**
     * Address of transaction initiator
     */
    public String sourceAddress;  // todo delete this

    public Transaction(TransactionOutput coinbaseOutput) {
        this.inputs = null;
        this.outputs = new ArrayList<>(1);
        outputs.add(coinbaseOutput);
        this.timestamp = System.currentTimeMillis();
        this.coinbase = true;
        this.fee = 0;
        this.hash = calculateHash();
    }

    public Transaction(ArrayList<TransactionInput> txInputs, ArrayList<TransactionOutput> txOutputs) {
        this.inputs = txInputs;
        this.outputs = txOutputs;
        this.timestamp = System.currentTimeMillis();
        this.coinbase = false;
        this.outputHash = Hash.hashString(this.outputs);
        this.fee = calculateFee();
        this.hash = calculateHash();
    }

    public Transaction() {
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    @TestOnly
    public Transaction(String hash) {
        this.hash = hash;
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    public String toString() {
        return Json.toJson(this);
    }

    public String calculateHash() {
        try {
            MessageDigest digest = null;
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash1 = digest.digest(this.toString().getBytes(StandardCharsets.UTF_8));
            byte[] hash2 = digest.digest(hash1);
            return Base64.getEncoder().encodeToString(hash2);
        } catch (NoSuchAlgorithmException ignored) {
        }
        return "";
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

    public ArrayList<TransactionInput> getInputs() {
        return inputs;
    }

    public void setInputs(ArrayList<TransactionInput> inputs) {
        this.inputs = inputs;
    }

    public ArrayList<TransactionOutput> getOutputs() {
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
