package blockchain.network;

import blockchain.data.core.Block;
import blockchain.data.core.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface INetwork {
    void registerCallback(Callback callback);

    void registerGlobalChainCallback(Callback callback);

    void unregisterCallback(Callback callback);

    void newBlock(Block block);

    void newTransaction(Transaction transaction);

    Map<String, Block> getBlock(String hash);

    Map<String, Map<Long, Block>> getBlockRange(Long heightMin, Long heightMax);

    String getUserLocation(String address);

    List<Transaction.Signature> moveUser(String address, String localChainId, List<Transaction.Signature> signatures);

    void globalNewBlock(Block block);

    void globalNewTransaction(Transaction transaction);

    String globalGetUserLocation(String address);

    void globalMoveUser(String address, String localChainId, List<Transaction.Signature> signatures);


    class Callback {
        public void onNewBlockReceived(Block data) {

        }

        public Transaction onNewTransactionRequested(String sourceAddress, String targetAddress, String value) {
            return null;
        }

        public void onSignedTransactionReceived(Transaction transaction) {

        }

        public void onPeerJoined(String nodeId) {

        }

        public void onPeerLost(String nodeId) {

        }

        public String onGetUserDataLocation(String address) {
            return "";
        }

        public List<Transaction.Signature> onMoveUser(String address,
                                                      String localChainId,
                                                      List<Transaction.Signature> signatures) {
            return new ArrayList<>();
        }

        public void onGlobalNewBlockReceived(Block data) {

        }

        public void onGlobalSignedTransactionReceived(Transaction transaction) {

        }

        public String onGlobalGetUserDataLocation(String address) {
            return "";
        }

        public List<Transaction.Signature> onGlobalMoveUser(String address,
                                                            String localChainId,
                                                            List<Transaction.Signature> signatures) {
            return new ArrayList<>();
        }

    }
}
