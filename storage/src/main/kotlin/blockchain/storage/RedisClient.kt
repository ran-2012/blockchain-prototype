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
        val address = utxo.publicKey
        val transactionStr = gson.toJson(utxo)
        jedis.sadd(addPrefix(PREFIX_ADDRESS, address), transactionStr)
        jedis.sadd(addPrefix(PREFIX_ADDRESS, "ALL"), address)
    }

    fun removeUtxo(utxo: Utxo) {
        val address = utxo.publicKey
        jedis.srem(addPrefix(PREFIX_ADDRESS, address), address)
    }

    fun getUtxo(address: String): Set<Utxo> {
        val set = jedis.smembers(addPrefix(PREFIX_ADDRESS, address)) ?: HashSet()
        val result = HashSet<Utxo>()
        for (str in set) {
            val utxo = gson.fromJson(str, Utxo::class.java)
            result.add(utxo)
        }
        return result
    }

    fun getAddressAll(): Set<String> {
        return jedis.smembers(addPrefix(PREFIX_ADDRESS, "ALL")) ?: HashSet()
    }

    fun getUtxoAll(): Set<Utxo> {
        val keys = jedis.smembers(addPrefix(PREFIX_UTXO, "ALL"))
        val result = HashSet<Utxo>()
        for (address in keys) {
            val set = jedis.smembers(addPrefix(PREFIX_ADDRESS, address))
            for (str in set) {
                result.add(gson.fromJson(str, Utxo::class.java))
            }
        }
        return result
    }

    fun setHeight(height: Long) {
        jedis.set(addPrefix("height"), height.toString())
    }

    fun getHeight(): Long {
        return jedis.get(addPrefix("height")).toLong()
    }

    @TestOnly
    fun cleanUp() {
        val keys = jedis.keys("$dataBaseName*")
        for (key in keys) {
            remove(key)
        }
    }
}