package blockchain.data.core;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;


public class Transaction {

    /**
     * Transaction hash
     */
    public String hash;

    ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    long timestamp;

    /**
     * Address of transaction initiator
     */
    public String sourceAddress;  // todo test

    public Transaction() {
        // this.hash = "";
        this.sourceAddress = "";  // todo test
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    public Transaction(String hash, String sourceAddress) {
        // this.hash = hash;  // todo
        this.sourceAddress = sourceAddress; // todo test
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    public Transaction(String hash, ArrayList<TransactionInput> inputs, ArrayList<TransactionOutput> outputs) {
        // this.hash = hash;  // todo
        this.inputs = inputs;
        this.outputs = outputs;
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    public String toString() {
        String str = "";
        for (TransactionInput txIn : inputs) {
            str = str.concat("input={").concat(txIn.contentString()).concat("}.");  // todo test
        }
        for (TransactionOutput txOut : outputs) {
            str = str.concat("output={").concat(txOut.contentString()).concat("}.");
        }
        str = str.concat("timestamp=").concat(Long.toString(timestamp));
        return str;
    }

    private String calculateHash() {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (digest != null) {
            byte[] hash1 = digest.digest(this.toString().getBytes(StandardCharsets.UTF_8));
            byte[] hash2 = digest.digest(hash1);  // todo test
            return Base64.getEncoder().encodeToString(hash2);
        } else {
            return "";
        }

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
