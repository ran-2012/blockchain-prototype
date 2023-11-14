package blockchain.data.core;

class TransactionInputOutputBase {

    String publicKey;  // use RSA public key
    String publicKeyHash;  // todo implement P2PKH
    long value;

    public TransactionInputOutputBase(String publicKey, long value) {
        this.publicKey = publicKey;
        this.value = value;
    }

    public TransactionInputOutputBase(String publicKey, String publicKeyHash, long value) {
        this.publicKey = publicKey;
        this.publicKeyHash = publicKeyHash;
        this.value = value;
    }

    @Override
    public String toString(){
        return "address=".concat(publicKey).concat("publicKeyHash=").concat(publicKeyHash).concat(",value=").concat(Long.toString(value));
    }

    public String contentString(){
        return "address=".concat(publicKey).concat(",value=").concat(Long.toString(value));  // todo add publicKeyHash
    }

    public String getPublicKey() {
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

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setValue(long value) {
        this.value = value;
    }

}
