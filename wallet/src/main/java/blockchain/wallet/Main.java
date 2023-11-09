package blockchain.wallet;

import blockchain.data.core.Transaction;
import blockchain.data.core.Utxo;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        if(args[0].equals("generate")){
            Wallet w = Wallet.generateWallet1();
            // write to config file
        }if(args[0].equals("ls")){
            // read config
        }if (args[0].equals("add")){
            String sk = args[1];
            String pk = args[2];
            // verify
            // write to config file
        }
        // delete

        // query balance
        // the address is acquired from argument
        HttpClientInternal client = new HttpClientInternal("localhost:7070");
        List<Utxo> list = client.getUtxoList("123");
        // Add all value and print

        // transfer
        // get source address adn target address and value from argument
        Transaction transaction = client.getTransaction("source", "target", 1);
        // add signature, further discussion required
        client.postSignedTransaction(transaction);

        // Addresses are hex, using Hex.encodeHexString
    }
}