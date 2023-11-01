package blockchain.storage

import blockchain.data.core.Transaction
import com.google.gson.Gson
import org.jetbrains.annotations.TestOnly
import redis.clients.jedis.Jedis

class RedisClient(val dataBaseName: String, private val port: Int = DEFAULT_PORT) {

    companion object {
        const val DEFAULT_PORT = 6379

        const val PREFIX_UTXO = "utxo"
        const val PREFIX_ADDRESS = "address"
    }

    private val jedis = Jedis("127.0.0.1", port)
    private val gson = Gson()

    init {
        jedis.connect()
    }

    private fun addPrefix(key: String): String {
        return "$dataBaseName:$key"
    }

    private fun addPrefix(prefix: String, key: String): String {
        return "$dataBaseName:$prefix:$key"
    }

    @TestOnly
    public fun getClient(): Jedis {
        return jedis
    }

    fun get(): Jedis {
        return jedis
    }

    fun get(key: String): String {
        return jedis.get(addPrefix(key)) ?: ""
    }

    fun set(key: String, value: String) {
        jedis.set(addPrefix(key), value)
    }

    fun remove(key: String) {
        jedis.del(addPrefix(key))
    }

    fun setAdd(key: String, value: String) {
        jedis.sadd(addPrefix(key), value)
    }

    fun setGet(key: String): MutableSet<String> {
        return jedis.smembers(addPrefix(key)) ?: HashSet()
    }

    fun setRemove(key: String, value: String) {
        jedis.srem(addPrefix(key), value)
    }

    fun addUtxo(address: String, transaction: Transaction) {
        val hash = transaction.hash
        val transactionStr = gson.toJson(transaction)
        jedis.set(addPrefix(PREFIX_UTXO, hash), transactionStr)
        jedis.sadd(addPrefix(PREFIX_ADDRESS, address), hash)
        jedis.sadd(addPrefix(PREFIX_ADDRESS, "ALL"), address)
    }

    fun removeUtxo(address: String, transaction: Transaction) {
        val hash = transaction.hash
        jedis.del(addPrefix(PREFIX_UTXO, hash))
        jedis.srem(addPrefix(PREFIX_ADDRESS, address), hash)
    }

    fun getUtxo(address: String): Set<String> {
        return jedis.smembers(addPrefix(PREFIX_ADDRESS, address)) ?: HashSet()
    }

    fun getAddress(): Set<String> {
        return jedis.smembers(addPrefix(PREFIX_ADDRESS, "ALL")) ?: HashSet()
    }
}