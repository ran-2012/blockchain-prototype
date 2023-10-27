package package blockchain.data.core;

import com.company.exceptions.*;

import java.util.Date;

public class Block {

    // 区块编号
    private int block_index;
    // 区块数据
    private Transaction[] block_data;
    // 区块时间戳
    private Date block_timestamp;
    // 前一区块哈希
    private String block_prev_hash;
    // 区块哈希
    private String block_hash;
    // 挖矿难度
    private int block_difficulty;
    // 随机数
    private int block_nonce;
    // Merkel Tree 根
    private String block_merkle_root;
    // 是否已经完成挖矿
    private boolean block_mined;

    /**
     * 构造函数，构造一个已经完成挖矿的区块。完成挖矿的区块不允许添加新的 transaction
     * @param index 区块编号
     * @param data 区块数据（Transaction）
     * @param timestamp 区块时间戳
     * @param prevHash 上一区块哈希
     * @param difficulty 难度
     * @param nonce 随机值
     * @throws TXEmptyException 传入的 transaction 为空时抛出
     * @throws TXNotEvenException 传入的 transaction 数量不是偶数时抛出
     * @throws NonceInvalidException 传入的 nonce 不满足难度要求时抛出
     */
    public Block(int index, Transaction[] data, Date timestamp, String prevHash, int difficulty, int nonce)
            throws TXNotEvenException, NonceInvalidException, TXEmptyException {
        ;
    }

    /**
     * 构造函数，构造一个尚未完成挖矿的区块。未完成挖矿的区块可以在之后添加 transaction
     * @param index 区块编号
     * @param data 区块数据（Transaction）
     * @param timestamp 区块时间戳
     * @param prevHash 前一区块哈希
     */
    public Block(int index, Transaction[] data, Date timestamp, String prevHash) {
        ;
    }

    /**
     * 构造函数，从 json 对象构造一个区块，应当是完成挖矿的区块。
     * @param jsonObject JSON对象
     * @throws TXEmptyException 传入的 transaction 为空时抛出
     * @throws TXNotEvenException 传入的 transaction 数量不是偶数时抛出
     * @throws MerkleTreeInvalidException 区块内的 transaction 与 merkle tree root 不符时抛出
     * @throws BlockInvalidException 区块哈希不符时抛出
     * @throws JSONInvalidException 传入的 json 不符合要求时抛出
     */
    public Block(Object jsonObject)
            throws TXNotEvenException, MerkleTreeInvalidException, BlockInvalidException, TXEmptyException, JSONInvalidException {
        ;
    }

    /**
     * 获取区块编号
     * @return 区块编号
     */
    public int getBlock_index() {
        return block_index;
    }

    /**
     * 获取所有 transaction
     * @return transaction 数组
     */
    public Transaction[] getBlock_data() {
        return block_data;
    }

    /**
     * 向区块内添加一个 transaction，仅限尚未完成挖矿的区块
     * @param tx 要添加的 transaction
     * @return 是否添加成功
     * @throws AlreadyMinedException 区块已完成挖矿时抛出
     */
    public boolean addTransaction(Transaction tx) throws AlreadyMinedException {
        return false;
    }

    /**
     * 获取区块时间戳
     * @return 时间戳
     */
    public Date getBlock_timestamp() {
        return block_timestamp;
    }

    /**
     * 获取前一区块哈希
     * @return 前一区块哈希
     */
    public String getBlock_prev_hash() {
        return block_prev_hash;
    }

    /**
     * 获取此区块哈希。若未完成挖矿则抛出异常
     * @return 此区块哈希
     * @throws NotMinedException 区块未完成挖矿时抛出
     */
    public String getBlock_hash() throws NotMinedException {
        return block_hash;
    }

    /**
     * 获取此区块 merkle tree root。若未完成挖矿则抛出异常
     * @return 此区块哈希
     * @throws NotMinedException 区块未完成挖矿时抛出
     */
    public String getBlock_merkle_root() throws NotMinedException {
        return block_merkle_root;
    }

    /**
     * 获取区块难度。若未完成挖矿则抛出异常
     * @return 难度
     * @throws NotMinedException 区块未完成挖矿时抛出
     */
    public int getBlock_difficulty() throws NotMinedException {
        return block_difficulty;
    }

    /**
     * 获取区块随机值。若未完成挖矿则抛出异常
     * @return 随机值
     * @throws NotMinedException 区块未完成挖矿时抛出
     */
    public int getBlock_nonce() throws NotMinedException {
        return block_nonce;
    }

    /**
     * 计算 merkle tree root，计算哈区块哈希并检查是否满足难度要求，若满足难度要求返回 true 并将区块设置为已完成挖矿
     * @param nonce 随机值
     * @return 是否成功
     * @throws AlreadyMinedException 区块已挖矿时抛出
     * @throws TXNotEvenException 区块内 transaction 数量不为偶数时抛出
     * @throws TXEmptyException 传入的 transaction 为空时抛出
     */
    public boolean mineBlock(int nonce) throws AlreadyMinedException, TXNotEvenException, TXEmptyException {
        return false;
    }

    /**
     * 此区块是否已完成挖矿
     * @return 是否已完成挖矿
     */
    public boolean isBlock_mined() {
        return block_mined;
    }

    /**
     * 将区块序列化为 JSON。不包含是否已挖矿字段。
     * @return JSON 对象
     * @throws NotMinedException 区块未挖矿时抛出
     */
    public Object block2JSON() throws NotMinedException {
        return null;
    }

    /**
     * 验证区块 merkel tree 以及哈希
     * @return 区块哈希以及 merkel tree root 是否正确
     * @throws NotMinedException 区块尚未挖矿时抛出
     * @throws TXNotEvenException 区块内 transaction 数量不为偶数时抛出
     * @throws MerkleTreeInvalidException 区块内 transaction 与 merkel tree root 不符时抛出
     * @throws BlockInvalidException 区块哈希不符时抛出
     */
    public boolean validate()
            throws NotMinedException, TXNotEvenException, MerkleTreeInvalidException, BlockInvalidException {
        return false;
    }
}
