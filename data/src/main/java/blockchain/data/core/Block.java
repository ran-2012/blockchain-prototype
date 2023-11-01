package blockchain.data.core;

import blockchain.data.exceptions.*;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Block {

    // 区块高度
    private long height;
    // 区块数据
    private ArrayList<Transaction> data;
    // 区块时间戳
    private Date timestamp;
    // 前一区块哈希
    private String prevHash;
    // 区块哈希（64个字符的hex字符串）
    private String hash;
    // 挖矿难度
    private int difficulty;
    // 随机数
    private long nonce;
    // Merkel Tree 根
    private String merkleRoot;

    public Block() throws TXNotEvenException, NonceInvalidException, TXEmptyException {
        this(0, "");
    }

    @TestOnly
    public Block(long height, String hash) throws TXNotEvenException, NonceInvalidException, TXEmptyException {
        this(height, new ArrayList<>(), new Date(), "", 0, 0);
        this.hash = hash;
    }

    /**
     * 构造函数，构造一个已经完成挖矿的区块。完成挖矿的区块不允许添加新的 transaction
     *
     * @param height     区块编号
     * @param data       区块数据（Transaction）
     * @param timestamp  区块时间戳
     * @param prevHash   上一区块哈希
     * @param difficulty 难度
     * @param nonce      随机值
     * @throws TXEmptyException      传入的 transaction 为空时抛出
     * @throws TXNotEvenException    传入的 transaction 数量不是偶数时抛出
     * @throws NonceInvalidException 传入的 nonce 不满足难度要求时抛出
     */
    public Block(long height, ArrayList<Transaction> data, Date timestamp, String prevHash, int difficulty, long nonce)
            throws TXNotEvenException, NonceInvalidException, TXEmptyException {
        this.height = height;
        this.data = data;
        this.timestamp = timestamp;
        this.prevHash = prevHash;
        this.difficulty = difficulty;
        this.nonce = nonce;

    }

    /**
     * 构造函数，构造一个尚未完成挖矿的区块。未完成挖矿的区块可以在之后添加 transaction
     *
     * @param height    区块编号
     * @param data      区块数据（Transaction）
     * @param timestamp 区块时间戳
     * @param prevHash  前一区块哈希
     */

    /**
     * 获取区块编号
     *
     * @return 区块编号
     */
    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public void setData(ArrayList<Transaction> data) {
        this.data = data;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setPrevHash(String prevHash) {
        this.prevHash = prevHash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public void setMerkleRoot(String merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    /**
     * 获取所有 transaction
     *
     * @return transaction 数组
     */
    public ArrayList<Transaction> getData() {
        return data;
    }

    /**
     * 向区块内添加一个 transaction，仅限尚未完成挖矿的区块
     *
     * @param tx 要添加的 transaction
     * @return 是否添加成功
     * @throws AlreadyMinedException 区块已完成挖矿时抛出
     */
    public boolean addTransaction(Transaction tx) throws AlreadyMinedException {
        return false;
    }

    /**
     * 获取区块时间戳
     *
     * @return 时间戳
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * 获取前一区块哈希
     *
     * @return 前一区块哈希
     */
    public String getPrevHash() {
        return prevHash;
    }

    /**
     * 获取此区块哈希。若未完成挖矿则抛出异常
     *
     * @return 此区块哈希
     * @throws NotMinedException 区块未完成挖矿时抛出
     */
    public String getHash() throws NotMinedException {
        return hash;
    }

    /**
     * 获取此区块 merkle tree root。若未完成挖矿则抛出异常
     *
     * @return 此区块哈希
     * @throws NotMinedException 区块未完成挖矿时抛出
     */
    public String getMerkleRoot() throws NotMinedException {
        return merkleRoot;
    }

    /**
     * 获取区块难度。若未完成挖矿则抛出异常
     *
     * @return 难度
     * @throws NotMinedException 区块未完成挖矿时抛出
     */
    public int getDifficulty() throws NotMinedException {
        return difficulty;
    }

    /**
     * 获取区块随机值。若未完成挖矿则抛出异常
     *
     * @return 随机值
     * @throws NotMinedException 区块未完成挖矿时抛出
     */
    public long getNonce() throws NotMinedException {
        return nonce;
    }

    /**
     * 设置 nonce
     *
     * @throws AlreadyMinedException 已经完成挖矿时抛出
     */
    public void setNonce() throws AlreadyMinedException {
        ;
    }

    /**
     * 更新区块 merkle tree root 值
     *
     * @return 更新后的 merkle tree root 值
     * @throws TXEmptyException   区块内 TX 为空时抛出
     * @throws TXNotEvenException 区块内 TX 数量不为偶数时抛出
     */
    public String updateMerkleRoot() throws TXEmptyException, TXNotEvenException {
        return "";
    }

    /**
     * 计算 merkle tree root，计算哈区块哈希并检查是否满足难度要求，若满足难度要求返回 true 并将区块设置为已完成挖矿
     *
     * @param nonce 随机值
     * @return 是否成功
     * @throws AlreadyMinedException 区块已挖矿时抛出
     * @throws TXNotEvenException    区块内 transaction 数量不为偶数时抛出
     * @throws TXEmptyException      传入的 transaction 为空时抛出
     */
    public boolean mineBlock(int nonce) throws AlreadyMinedException, TXNotEvenException, TXEmptyException {
        return false;
    }

    /**
     * 验证区块 merkel tree 以及哈希
     *
     * @return 区块哈希以及 merkel tree root 是否正确
     * @throws NotMinedException          区块尚未挖矿时抛出
     * @throws TXNotEvenException         区块内 transaction 数量不为偶数时抛出
     * @throws MerkleTreeInvalidException 区块内 transaction 与 merkel tree root 不符时抛出
     * @throws BlockInvalidException      区块哈希不符时抛出
     */
    public boolean validate()
            throws NotMinedException, TXNotEvenException, MerkleTreeInvalidException, BlockInvalidException {
        return false;
    }
}
