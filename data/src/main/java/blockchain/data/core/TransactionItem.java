package blockchain.data.core;

public class TransactionItem {

    String address;
    int value;
    String signature;

    public TransactionItem(String address, int value, String signature) {
        this.address = address;
        this.value = value;
        this.signature = signature;
    }

    public String getAddress() {
        return address;
    }


    public int getValue() {
        return value;
    }

    public String getSignature() {
        return signature;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
