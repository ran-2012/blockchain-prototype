package blockchain.data.core;

import blockchain.utility.Json;

class TransactionInputOutputBase {

    public String address;
    public long value;

    public TransactionInputOutputBase(String address, long value) {
        this.address = address;
        this.value = value;
    }

    @Override
    public String toString(){
        return Json.toJson(this);
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

    public void setValue(long value) {
        this.value = value;
    }

}
