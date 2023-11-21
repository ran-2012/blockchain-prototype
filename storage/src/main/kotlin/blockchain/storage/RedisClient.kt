package blockchain.storage

import blockchain.data.core.TransactionInput
import blockchain.data.core.TransactionOutput
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

    val normal = Internal()
    val pending = Internal("$dataBaseName:pending")

    init {
        jedis.connect()
    }

    @TestOnly
    public fun getClient(): Jedis {
        return jedis
    }

    inner class Internal(private val prefix: String = dataBaseName) {
        private fun addPrefix(vararg key: String): String {
            var result = prefix
            for (k in key) {
                result = "$result:$key"
            }
            return result.removeSuffix(":")
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

        fun addUtxo(transactionId: String, outputIdx: Int, utxo: TransactionOutput) {
            val address = utxo.address
            val utxoStr = gson.toJson(utxo)

            val utxoId = "$transactionId:$outputIdx"
            jedis.set(addPrefix(PREFIX_UTXO, utxoId), utxoStr)
            jedis.sadd(addPrefix(PREFIX_ADDRESS, address), utxoId)
            jedis.sadd(addPrefix(PREFIX_ADDRESS, "ALL"), address)
        }

        fun removeUtxo(address: String, transactionId: String, outputIdx: Int) {
            val utxoId = "$transactionId:$outputIdx"
            jedis.del(addPrefix(PREFIX_UTXO, utxoId))
            jedis.srem(addPrefix(PREFIX_ADDRESS, address), utxoId)
        }

        fun getUtxo(address: String): Set<TransactionInput> {
            val set = jedis.smembers(addPrefix(PREFIX_ADDRESS, address)) ?: HashSet()
            val result = HashSet<TransactionInput>()
            for (str in set) {
                val utxo = gson.fromJson(str, TransactionInput::class.java)
                val utxoIdSplit = str.split(":")
                utxo.originalTxHash = utxoIdSplit[0]
                utxo.originalOutputIndex = utxoIdSplit[1].toInt()
                result.add(utxo)
            }
            return result
        }

        fun getAddressAll(): Set<String> {
            return jedis.smembers(addPrefix(PREFIX_ADDRESS, "ALL")) ?: HashSet()
        }

        fun getUtxoAll(): Set<TransactionOutput> {
            val keys = jedis.smembers(addPrefix(PREFIX_UTXO, "ALL"))
            val result = HashSet<TransactionOutput>()
            for (address in keys) {
                val set = jedis.smembers(addPrefix(PREFIX_ADDRESS, address))
                for (str in set) {
                    result.add(gson.fromJson(str, TransactionOutput::class.java))
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
    }

    @TestOnly
    fun cleanUp() {
        val keys = jedis.keys("$dataBaseName*")
        for (key in keys) {
            jedis.del(key)
        }
    }
}