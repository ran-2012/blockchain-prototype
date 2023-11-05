package blockchain.data.core;

class TransactionInputOutputBase {

    String address;
    long value;
    String signature;

    public TransactionInputOutputBase(String address, int value, String signature) {
        this.address = address;
        this.value = value;
        this.signature = signature;
    }

    @Override
    public String toString(){
        return "address=".concat(address).concat(",value=").concat(Long.toString(value)).
                concat(",signature=").concat(signature);  // todo signature should not be included for hash?
    }

    public String contentString(){
        return "address=".concat(address).concat(",value=").concat(Long.toString(value));  // without signature
    }

    public String getAddress() {
        return address;
    }

    public long getValue() {
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
