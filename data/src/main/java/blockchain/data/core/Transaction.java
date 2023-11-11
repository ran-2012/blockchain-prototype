package blockchain.data.core;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;


public class Transaction {

    /**
     * Transaction hash（txid）
     */
    public String hash; //todo rename hash to txId

    String txId;

    ArrayList<TransactionInput> inputs = new ArrayList<>();
    ArrayList<TransactionOutput> outputs = new ArrayList<>();

    long fee;
    long timestamp;

    boolean coinbase;

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
        this.fee = 0;  // todo the fee of the coinbase transaction itself is set to zero, the total fee of the block is the sum of the fees of all the other transactions
        this.txId = calculateHash();
        this.hash = calculateHash();  // todo delete this line
    }

    public Transaction(ArrayList<TransactionInput> txInputs, ArrayList<TransactionOutput> txOutputs) {
        this.inputs = txInputs;
        this.outputs = txOutputs;
        this.timestamp = System.currentTimeMillis();
        this.coinbase = false;
        this.fee = calculateFee();
        this.txId = calculateHash();
        this.hash = calculateHash();  // todo delete this line
    }



    public Transaction() {  // todo delete this
        // this.hash = "";
        this.sourceAddress = "";  // todo test
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    public Transaction(String hash, String sourceAddress) {// todo delete this
        // this.hash = hash;  // todo
        this.sourceAddress = sourceAddress; // todo test
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    public Transaction(String hash, ArrayList<TransactionInput> inputs, ArrayList<TransactionOutput> outputs) {// todo delete this
        // this.hash = hash;  // todo
        this.inputs = inputs;
        this.outputs = outputs;
        this.timestamp = System.currentTimeMillis();
        this.hash = calculateHash();
    }

    public String toString() {
        String str = "";
        if (inputs != null ) {  // if this transaction is a coinbase transaction, inputs == null
            for (TransactionInput txIn : inputs) {
                str = str.concat("input={").concat(txIn.contentString()).concat("}.");  // todo test
            }
        }
        for (TransactionOutput txOut : outputs) {
            str = str.concat("output={").concat(txOut.contentString()).concat("}.");
        }
        str = str.concat("timestamp=").concat(Long.toString(timestamp));
        return str;
    }

    public String calculateHash() {
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

    public long calculateFee() {
        long fee = 0;
        for (TransactionInput txIn : inputs) {
            fee = fee + txIn.getValue();
        }
        for (TransactionOutput txOut : outputs) {
            fee = fee - txOut.getValue();
        }
        return fee;  // todo fee < 0 error
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
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

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }
}
