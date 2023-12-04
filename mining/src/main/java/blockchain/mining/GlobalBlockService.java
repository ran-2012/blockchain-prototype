package blockchain.mining;

import blockchain.data.core.Block;
import blockchain.data.core.Transaction;
import blockchain.data.core.TransactionInput;
import blockchain.data.core.TransactionOutput;
import blockchain.network.INetwork;
import blockchain.storage.IStorage;
import blockchain.storage.Storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GlobalBlockService extends BaseBlockService {

    private final IStorage storage = Storage.getGlobalInstance();
    private final INetwork.Callback networkCallback = new INetwork.Callback() {
        @Override
        public void onGlobalNewBlockReceived(Block data) {
            addNewBlock(data);
        }

        @Override
        public void onGlobalSignedTransactionReceived(Transaction transaction) {
            addNewTransaction(transaction);
        }

        @Override
        public List<Transaction.Signature> onGlobalMoveUser(String address, String localChainId, List<Transaction.Signature> signatures) {
            if (callback != null) {
                return callback.onMoveUser(address, localChainId, signatures);
            }
            return new ArrayList<>();
        }

        @Override
        public String onGlobalGetUserDataLocation(String address) {
            if (callback != null) {
                return callback.onGetUserDataLocation(address);
            }
            return "";
        }
    };

    public GlobalBlockService(boolean isMiner, String publicKey, String privateKey) {
        super(isMiner, publicKey, privateKey);
    }

    public Transaction generateNewTransaction(String sourceAddress, String targetAddress, String data, String localChainId) {
        Transaction transaction = super.generateNewTransaction(sourceAddress, targetAddress, data);
        TransactionOutput output = transaction.outputs.get(0);
        output.data = data;
        output.localChainId = localChainId;
        return transaction;
    }

    @Override
    protected IStorage getStorage() {
        return storage;
    }

    @Override
    protected INetwork getNetwork() {
        return super.getNetwork();
    }

    @Override
    protected INetwork.Callback getNetworkCallback() {
        return networkCallback;
    }
}
