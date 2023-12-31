package blockchain.data.core;

import blockchain.data.exceptions.*;
import blockchain.utility.Hash;
import blockchain.utility.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Block {

    // 区块高度
    private long height;
    // 区块数据
    private List<Transaction> data;
    // 区块时间戳
    private long timestamp;
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

    /**
     * 空构造函数
     */
    public Block() {
        height = 0;
        data = new ArrayList<>();
        timestamp = new Date().getTime();
        prevHash = "";
        hash = "";
        difficulty = 1;
        nonce = 0;
        merkleRoot = "";
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
    public Block(int height, List<Transaction> data, long timestamp, String prevHash, int difficulty, int nonce)
            throws TXNotEvenException, NonceInvalidException, TXEmptyException, NoSuchAlgorithmException {
        this.height = height;
        this.data = data;
        this.timestamp = timestamp;
        this.prevHash = prevHash;
        this.difficulty = difficulty;
        this.nonce = nonce;
        updateMerkleRoot();
        updateBlockHash();
    }

    /**
     * 构造函数，构造一个尚未完成挖矿的区块。未完成挖矿的区块可以在之后添加 transaction
     *
     * @param height    区块编号
     * @param data      区块数据（Transaction）
     * @param timestamp 区块时间戳
     * @param prevHash  前一区块哈希
     */
    public Block(int height, ArrayList<Transaction> data, long timestamp, String prevHash) {
        this.height = height;
        this.data = data;
        this.timestamp = timestamp;
        this.prevHash = prevHash;
        this.difficulty = 1;
        this.merkleRoot = "";
        this.hash = "";
        this.nonce = 0;
    }

    /**
     * 获取区块高度
     *
     * @return 区块高度
     */
    public long getHeight() {
        return height;
    }

    /**
     * 设置区块编号
     *
     * @param height 区块高度
     */
    public void setHeight(long height) {
        this.height = height;
    }

    /**
     * 获取所有 transaction
     *
     * @return transaction 数组
     */
    public List<Transaction> getData() {
        return data;
    }

    /**
     * 设置区块内的交易列表
     *
     * @param data 交易列表
     */
    public void setData(ArrayList<Transaction> data) {
        this.data = data;
    }

    /**
     * 向区块内添加一个 transaction，仅限尚未完成挖矿的区块
     *
     * @param tx 要添加的 transaction
     */
    public void addTransaction(Transaction tx) {
        data.add(tx);
    }

    /**
     * 获取区块时间戳
     *
     * @return 时间戳
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * 设置时间戳
     *
     * @param timestamp 时间戳
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
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
     * 设置前一区块哈希
     *
     * @param prevHash 前一区块哈希
     */
    public void setPrevHash(String prevHash) {
        this.prevHash = prevHash;
    }

    /**
     * 获取此区块哈希。若未完成挖矿则抛出异常
     *
     * @return 此区块哈希
     */
    public String getHash() {
        return hash;
    }

    /**
     * **慎用** 设置此区块哈希
     *
     * @param hash 哈希
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * 获取此区块 merkle tree root。若未完成挖矿则抛出异常
     *
     * @return 此区块哈希
     */
    public String getMerkleRoot() {
        return merkleRoot;
    }

    /**
     * **慎用**
     * 常规情况下请调用 updateMerkleRoot 方法
     * 设置此区块 merkle root 值
     *
     * @param merkleRoot merkle root 值
     */
    public void setMerkleRoot(String merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    /**
     * 获取区块难度。若未完成挖矿则抛出异常
     *
     * @return 难度
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * 设置区块难度
     *
     * @param difficulty 难度
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * 获取区块随机值。若未完成挖矿则抛出异常
     *
     * @return 随机值
     */
    public long getNonce() {
        return nonce;
    }

    /**
     * **慎用**
     * 挖矿请调用 mineBlock 方法
     * 设置随机值
     */
    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    /**
     * 更新区块 merkle tree root 值
     */
    public void updateMerkleRoot() {
        if (data.isEmpty()) {
            merkleRoot = "";
        }
        List<Transaction> txs = new ArrayList<>(data);
        int pow = 0;
        while (Math.pow(2, pow) < txs.size()) {
            pow += 1;
        }
        for (int i = 0; i < (Math.pow(2, pow) - txs.size()); i++) {
            txs.add(new Transaction());
        }
        String[] merkleTree = new String[2 * data.size() - 1];
        for (int i = 0; i < data.size(); i++) {
            merkleTree[i + data.size() - 1] = data.get(i).getHash();
        }
        while (pow > 0) {
            int startIndex = (int) Math.pow(2, pow) - 1;
            int dataNum = startIndex + 1;
            for (int i = startIndex; i < startIndex + dataNum - 1; i += 2) {
                try {
                    merkleTree[(i - 1) / 2] = Hash.hashString(merkleTree[i] + merkleTree[i + 1]);
                } catch (Exception ex) {
                    merkleTree[(i - 1) / 2] = "";
                }

            }
            pow--;
        }
        merkleRoot = merkleTree[0];
    }

    /**
     * 更新区块哈希
     *
     * @return 更新后的哈希
     * @throws NonceInvalidException nonce 不合法时抛出
     */
    public String updateBlockHash() throws NonceInvalidException, TXEmptyException {
        if (merkleRoot.isEmpty()) {
            updateMerkleRoot();
        }
        String headerStr = getHeaderString();
        Map<String, Object> hashResult;
        try {
            hashResult = Hash.hashAndCount0(headerStr);
        } catch (NoSuchAlgorithmException e) {
            Log log = Log.get(this);
            log.error("Cannot find instance SHA-256");
            return "";
        }
        if ((int) (hashResult.get("NumOfZero")) >= difficulty) {
            this.hash = hashResult.get("Hash").toString();
            return this.hash;
        } else {
            throw new NonceInvalidException();
        }
    }

    /**
     * 计算 merkle tree root，计算哈区块哈希并检查是否满足难度要求，若满足难度要求返回 true 并将区块设置为已完成挖矿
     *
     * @param nonce 随机值
     * @return 是否成功
     */
    public boolean mineBlock(long nonce) {
        this.nonce = nonce;

        Map<String, Object> hashResult = getHeaderHashMap();
        if (hashResult == null) return false;
        if ((int) (hashResult.get("NumOfZero")) >= difficulty) {
            this.hash = hashResult.get("Hash").toString();
            this.nonce = nonce;
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    private Map<String, Object> getHeaderHashMap() {
        String headerStr = getHeaderString();
        Map<String, Object> hashResult;
        try {
            hashResult = Hash.hashAndCount0(headerStr);
        } catch (NoSuchAlgorithmException e) {
            Log log = Log.get(this);
            log.error("Cannot find instance SHA-256");
            return null;
        }
        return hashResult;
    }

    @NotNull
    private String getHeaderString() {
        return String.valueOf(height) + timestamp + prevHash +
                difficulty + nonce + merkleRoot;
    }

    /**
     * 验证区块 merkel tree 以及哈希
     *
     * @return 区块哈希以及 merkel tree root 是否正确
     * @throws NotMinedException          区块尚未挖矿时抛出
     * @throws MerkleTreeInvalidException 区块内 transaction 与 merkel tree root 不符时抛出
     * @throws BlockInvalidException      区块哈希不符时抛出
     * @throws NonceInvalidException      区块哈希不符难度时抛出
     */
    public boolean validate()
            throws NotMinedException, MerkleTreeInvalidException, BlockInvalidException, TXEmptyException, NonceInvalidException {
        if (this.hash.isEmpty()) {
            throw new NotMinedException();
        }
        String oldMerkleRoot = this.merkleRoot;
        updateMerkleRoot();
        if (!Objects.equals(oldMerkleRoot, this.merkleRoot)) {
            this.merkleRoot = oldMerkleRoot;
            throw new MerkleTreeInvalidException();
        }
        Map<String, Object> hashResult = getHeaderHashMap();
        if (hashResult == null) return false;
        if ((int) (hashResult.get("NumOfZero")) < difficulty) {
            throw new NonceInvalidException();
        }
        if (!Objects.equals(hash, hashResult.get("Hash").toString())) {
            throw new BlockInvalidException();
        }
        return true;
    }
}
