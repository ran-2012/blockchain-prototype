package blockchain.storage

import blockchain.data.core.Utxo
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

    fun addUtxo(utxo: Utxo) {
        val address = utxo.address
        val hash = utxo.signature
        val transactionStr = gson.toJson(utxo)
        jedis.set(addPrefix(PREFIX_UTXO, hash), transactionStr)
        jedis.sadd(addPrefix(PREFIX_ADDRESS, address), hash)
        jedis.sadd(addPrefix(PREFIX_ADDRESS, "ALL"), address)
    }

    fun removeUtxo(utxo: Utxo) {
        val address = utxo.address
        val hash = utxo.signature
        jedis.del(addPrefix(PREFIX_UTXO, hash))
        jedis.srem(addPrefix(PREFIX_ADDRESS, address), hash)
    }

    fun getUtxoByHash(hash: String): Utxo? {
        val utxoStr = jedis.get(addPrefix(PREFIX_UTXO, hash))
        return if (utxoStr.isEmpty()) {
            null
        } else {
            gson.fromJson(utxoStr, Utxo::class.java)
        }
    }

    fun getUtxo(address: String): Set<Utxo> {
        val set = jedis.smembers(addPrefix(PREFIX_ADDRESS, address)) ?: HashSet()
        val result = HashSet<Utxo>()
        for (hash in set) {
            val utxo = gson.fromJson(jedis.get(addPrefix(PREFIX_UTXO, hash)), Utxo::class.java)
            result.add(utxo)
        }
        return result
    }

    fun getAddressAll(): Set<String> {
        return jedis.smembers(addPrefix(PREFIX_ADDRESS, "ALL")) ?: HashSet()
    }

    fun getUtxoAll(): Set<Utxo> {
        val keys = jedis.keys(addPrefix(PREFIX_UTXO, "*"))
        val result = HashSet<Utxo>()
        for (key in keys) {
            val utxoStr = jedis.get(key)
            if (utxoStr.isNotEmpty()) {
                val utxo = gson.fromJson(utxoStr, Utxo::class.java)
                result.add(utxo)
            }
        }
        return result
    }

    @TestOnly
    fun cleanUp() {
        val keys = jedis.keys("$dataBaseName*")
        for (key in keys) {
            remove(key)
        }
    }
}