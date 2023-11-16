package blockchain.data.core;

class TransactionInputOutputBase {

    String publicKey;  // use RSA public key
    String address;
    long value;

    public TransactionInputOutputBase(String publicKey, long value) {
        this.publicKey = publicKey;
        this.value = value;
    }

    public TransactionInputOutputBase(String publicKey, String address, long value) {
        this.publicKey = publicKey;
        this.address = address;
        this.value = value;
    }

    @Override
    public String toString() {
        return "address=".concat(publicKey).concat("publicKeyHash=").concat(address).concat(",value=").concat(Long.toString(value));
    }

    public String contentString() {
        return "address=".concat(publicKey).concat(",value=").concat(Long.toString(value));  // todo add publicKeyHash
    }

    public String getPublicKey() {
        return publicKey;
    }

    public long getValue() {
        return value;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setValue(long value) {
        this.value = value;
    }

}
