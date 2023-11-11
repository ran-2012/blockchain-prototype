/**
 * 区块链核心服务
 *
 * @author YUAN Longhang
 */
package blockchain.mining;

import blockchain.data.core.Block;
import blockchain.data.core.Transaction;
import blockchain.data.core.TransactionInput;
import blockchain.data.core.TransactionOutput;
import blockchain.storage.Storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockService {

    /**
     * 创建创世区块
     *
     * @return
     */

    public Block createGenesisBlock() {
        Block genesisBlock = new Block();
        //设置创世区块高度为1
        genesisBlock.setHeight(1);
        genesisBlock.setTimestamp(System.currentTimeMillis());
        genesisBlock.setDifficulty(1);
        //封装第一笔交易,硬编码
        ArrayList<TransactionInput> txInputList = new ArrayList<TransactionInput>();
        ArrayList<TransactionOutput> txOutputList = new ArrayList<TransactionOutput>();
        ArrayList<Transaction> txList = new ArrayList<Transaction>();
        Transaction tx = new Transaction("000000000000000000000000000000000000000000000000000000000000000", txInputList, txOutputList);
        txList.add(tx);
        genesisBlock.setData(txList);
        //设置创世区块的hash值
        try {
            genesisBlock.updateBlockHash();
        } catch (Exception ignore) {
        }
        //添加到区块链存储中
        BlockCache.addBlockSync(genesisBlock);
        System.out.println("创世区块成功创建并存储");
        return genesisBlock;
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
            for (Transaction transaction : newBlock.getData()) {
                BlockCache.addTransactionSync(transaction);
            }
            return true;
        }
        return false;
    }

    public Block getblock(long height) {
        // 获取指定高度的区块
        Map<Long, List<Block>> blockMap = Storage.getInstance().getBlockRange(height, height);
        List<Block> blockList = blockMap.get(height);

        // 检查是否存在指定高度的区块
        if (blockList != null && !blockList.isEmpty()) {
            Block block = blockList.get(0);
            String blockHash = block.getHash();
            System.out.println("Block hash at height" + height + ": " + blockHash);
            return block;
        } else {
            System.out.println("Block at height" + height + "not found");
        }
        return null;
    }

    public boolean isValidNewBlock(Block newBlock) {
        if (!getblock(newBlock.getHeight() - 1).getHash().equals(newBlock.getPrevHash())) {
            System.out.println("新区块的前一个区块hash验证不通过");
            return false;
        } else {
            try {
                return newBlock.validate();
            } catch (Exception ignore) {
                return false;
            }
        }
    }

}