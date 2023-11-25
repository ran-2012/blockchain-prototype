package blockchain.mining;

import blockchain.data.core.Block;
import blockchain.data.core.Transaction;
import blockchain.data.exceptions.NonceInvalidException;
import blockchain.data.exceptions.TXEmptyException;
import blockchain.data.exceptions.TXNotEvenException;
import blockchain.utility.Log;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

class MiningServiceTest {
    Log log = Log.get(this);

    @Test
    void testHashTimeConsumption() throws TXNotEvenException, NonceInvalidException, TXEmptyException, NoSuchAlgorithmException {
        long beforeTime = System.currentTimeMillis();
        log.info("Before: {}", beforeTime);

        List<Transaction> list = new ArrayList<>();
        list.add(new Transaction("1"));
        list.add(new Transaction("2"));
        list.add(new Transaction("3"));
        list.add(new Transaction("4"));
        Block block = new Block(0, list, System.currentTimeMillis(), "", 0, 0);
        long times = (long) Math.pow(2, 20);
        log.info("hash for {} rounds", times);
        for (long i = 0; i < times; ++i) {
            block.mineBlock(i);
        }
        long afterTime = System.currentTimeMillis();
        log.info("After: {}", afterTime);
        log.info("Time used: {}", afterTime - beforeTime);
    }
}