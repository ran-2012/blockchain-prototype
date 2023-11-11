package blockchain.data.core;

class TransactionInputOutputBase {

    long publicKey;  // use RSA public key
    String publicKeyHash;  // todo implement P2PKH
    long value;

    public TransactionInputOutputBase(long publicKey, long value) {
        this.publicKey = publicKey;
        this.value = value;
    }

    public TransactionInputOutputBase(long publicKey, String publicKeyHash, long value) {
        this.publicKey = publicKey;
        this.publicKeyHash = publicKeyHash;
        this.value = value;
    }

    @Override
    public String toString(){
        return "address=".concat(Long.toString(publicKey)).concat("publicKeyHash=").concat(publicKeyHash).concat(",value=").concat(Long.toString(value));
    }

    public String contentString(){
        return "address=".concat(Long.toString(publicKey)).concat(",value=").concat(Long.toString(value));  // todo add publicKeyHash
    }

    public long getPublicKey() {
        return publicKey;
    }

    public long getValue() {
        return value;
    }

    public String getPublicKeyHash() {
        return publicKeyHash;
    }

    public void setPublicKeyHash(String publicKeyHash) {
        this.publicKeyHash = publicKeyHash;
    }

    public void setPublicKey(long publicKey) {
        this.publicKey = publicKey;
    }

    public void setValue(long value) {
        this.value = value;
    }

}
