package blockchain.mining;

public class BlockController {


    BlockService blockService;
    /**
     * 创建创世区块
     */
    public void createFirstBlock() {
        blockService.createGenesisBlock();
    }
}