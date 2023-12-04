/**
 * 区块链核心服务
 *
 * @author YUAN Longhang
 */
package blockchain.mining;

import blockchain.data.core.Block;
import blockchain.data.core.Transaction;
import blockchain.network.INetwork;
import blockchain.network.Network;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class LocalBlockService extends BaseBlockService {

    private final INetwork.Callback networkCallback = new INetwork.Callback() {
        @Override
        public void onNewBlockReceived(Block data) {
            addNewBlock(data);
        }

        @Override
        public Transaction onNewTransactionRequested(String sourceAddress, String targetAddress, String value) {
            return generateNewTransaction(sourceAddress, targetAddress, value);
        }

        @Override
        public void onSignedTransactionReceived(Transaction transaction) {
            if (getStorage().hasAddress(transaction.sourceAddress)) {
                addNewTransaction(transaction);
            }
            if (Config.global.isInGlobalChain) {

            } else {
                getNetwork().moveUser(transaction.sourceAddress, Config.global.localChainId, transaction.signatures);
            }
        }


        @Override
        public List<Transaction.Signature> onMoveUser(String address, String localChainId, List<Transaction.Signature> signatures) {
            if (callback != null) {
                return callback.onMoveUser(address, localChainId, signatures);
            }
            return new ArrayList<>();
        }

        @Override
        public String onGetUserDataLocation(String address) {
            if (callback != null) {
                return callback.onGetUserDataLocation(address);
            }
            return "";
        }

    };

    public LocalBlockService() {
        this(false, "");
    }

    public LocalBlockService(boolean isMiner, String publicKey) {
        this(isMiner, publicKey, "");
    }

    public LocalBlockService(boolean isMiner, String publicKey, String privateKey) {
        super(isMiner, publicKey, privateKey);

        Network.getInstance().registerCallback(getNetworkCallback());
    }


    @Override
    protected INetwork.Callback getNetworkCallback() {
        return networkCallback;
    }

}