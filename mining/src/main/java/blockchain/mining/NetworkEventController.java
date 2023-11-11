package blockchain.mining;

import blockchain.data.core.Block;
import blockchain.data.core.Transaction;
import blockchain.network.INetwork;

public class NetworkEventController extends INetwork.Callback {
    @Override
    public void onNewBlockReceived(Block data) {
        try {
            data.validate();
        } catch (Exception ignored) {
        }


    }
    @Override
    public Transaction onNewTransactionRequested(String sourceAddress, String targetAddress, Long value) {
        return super.onNewTransactionRequested(sourceAddress, targetAddress, value);
    }

    @Override
    public void onSignedTransactionReceived(Transaction transaction) {
        super.onSignedTransactionReceived(transaction);
    }

    @Override
    public void onPeerJoined(String nodeId) {
        super.onPeerJoined(nodeId);
    }

    @Override
    public void onPeerLost(String nodeId) {
        super.onPeerLost(nodeId);
    }
}
