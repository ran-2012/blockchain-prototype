package blockchain.mining;
import blockchain.data.core.*;
import java.util.ArrayList;
import blockchain.utility.*;
import blockchain.storage.*;
import io.javalin.json.JsonMapper;

import java.util.ArrayList;
import java.util.StringJoiner;
/**
 * 共识机制
 * 采用POW即工作量证明实现共识
 * @author YUAN Longhang
 *
 */
public class PowService {

    BlockCache blockCache;
    BlockService blockService;
    public void consensus(){

        if(blockService.getblock(newBlock().getHeight() - 1)
    }





    /**
     * 通过“挖矿”进行工作量证明，实现节点间的共识
     *
     * @return
     */
    public Block mine(){

        // 封装交易数据集合
        ArrayList<Transaction> txList = new ArrayList<Transaction>();
        Transaction tx1 = new Transaction();


        // 定义每次哈希函数的结果
        String newBlockHash = "";
        int nonce = 0;
        long start = System.currentTimeMillis();
        System.out.println("开始挖矿");
        while (true) {
            // 计算新区块hash值
            newBlockHash = blockService.Hash(blockCache.getLatestBlock().getHash(), tsaList, nonce);
            // 校验hash值
            if (blockService.isValidHash(newBlockHash)) {
                System.out.println("挖矿完成，正确的hash值：" + newBlockHash);
                System.out.println("挖矿耗费时间：" + (System.currentTimeMillis() - start) + "ms");
                break;
            }
            System.out.println("第"+(nonce+1)+"次尝试计算的hash值：" + newBlockHash);
            nonce++;
        }
        // 创建新的区块
        Block block = blockService.createNewBlock(nonce, blockCache.getLatestBlock().getHash(), newBlockHash, tsaList);
        return block;
    }

    /**
     * 验证hash值是否满足系统条件
     * 暂定前4位是0则满足条件
     * @param hash
     * @return
     */
    public boolean isValidHash(String hash) {
        //System.out.println("难度系数："+blockCache.getDifficulty());
        return hash.startsWith("0000");
    }
}