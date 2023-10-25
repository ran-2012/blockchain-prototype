package blockchain.data.wallet;

import blockchain.data.core.Response;

public class BalanceResponse extends Response {
    public Long balance;
    public Long blockId;
    public String blockHash;
}
