/**
 * 区块链核心服务
 *
 * @author YUAN Longhang
 *
 */
package blockchain.mining;
import blockchain.data.core.*;
import java.util.ArrayList;
import blockchain.utility.*;
import blockchain.storage.*;
import io.javalin.json.JsonMapper;

import java.util.ArrayList;
import java.util.StringJoiner;
public class BlockService {

    /**
     * 创建创世区块
     * @return
     */

    public String createGenesisBlock() {
        Block genesisBlock = new Block();
        //设置创世区块高度为1
        genesisBlock.setHeight(1);
        genesisBlock.setTimestamp(System.currentTimeMillis());
        genesisBlock.setDifficulty(1);
        //封装第一笔交易,硬编码
        ArrayList<TransactionInput> txInputList = new ArrayList<TransactionInput>();
        ArrayList<TransactionOutput> txOutputList = new ArrayList<TransactionOutput>();
        ArrayList<Transaction> txList = new ArrayList<Transaction>();
        Transaction tx = new Transaction("000000000000000000000000000000000000000000000000000000000000000",txInputList,txOutputList);
        txList.add(tx);
        genesisBlock.setData(txList);
        //设置创世区块的hash值
        genesisBlock.setHash(Hash.hash(txList);
        //添加到区块链存储中
        BlockCache.addBlockSync(genesisBlock);
        System.out.println("创世区块成功创建并存储");
        return Json.Json(genesisBlock);
    }

    /**
     * 添加新区块到当前节点的区块链中
     *
     * @param newBlock
     */
    public boolean addBlock(Block newBlock) {
        //先对新区块的合法性进行校验
        if (isValidNewBlock(newBlock)) {
            BlockCache.addBlockSync(newBlock);
            // 新区块的业务数据需要加入到已打包的业务数据集合里去
            BlockCache.addTransactionSync(newBlock.getData());
            return true;
        }
        return false;
    }
    public  Block getblock(long height) {
        // 获取指定高度的区块
        Map<Long, List<Block>> blockMap = storage.getBlockRangeSync(height, height);
        List<Block> blockList = blockMap.get(height);

        // 检查是否存在指定高度的区块
        if (blockList != null && !blockList.isEmpty()) {
            Block block = blockList.get(0);
            String blockHash = block.getHash();
            System.out.println("Block hash at height" + height + ": " + blockHash);
        } else {
            System.out.println("Block at height" + height + "not found");
        }
        /**
         * 验证新区块是否有效
         *
         * @param newBlock
         * @param previousBlock
         * @return
         */
        public boolean isValidNewBlock (Block newBlock){
            if (!getblock(newBlock().getHeight() - 1).getHash().equals(newBlock.getPreviousHash())) {
                System.out.println("新区块的前一个区块hash验证不通过");
                return false;
            } else {
                // 验证新区块hash值的正确性
                String hash = Hash.hash(newBlock.getPreviousHash(), newBlock.getTransactions(), newBlock.getNonce());
                if (!hash.equals(newBlock.getHash())) {
                    System.out.println("新区块的hash无效: " + hash + " " + newBlock.getHash());
                    return false;
                }
                if (!isValidHash(newBlock.getHash())) {
                    return false;
                }
            }

            return true;
        }

    }