package blockchain.data.wallet;

import blockchain.data.core.Response;

public class TransactionResponse extends Response {

    public Status status;

    public enum Status {
        ACCEPTED,
        REJECTED
    }
}
