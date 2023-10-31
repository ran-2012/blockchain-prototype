package blockchain.storage

import blockchain.data.core.Transaction
import com.mongodb.ConnectionString
import com.mongodb.client.model.Indexes
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.*

class DataBaseClient(dataBaseName: String, dbUri: String = DEFAULT_DB_URI) {

    companion object {
        const val DEFAULT_DB_URI = "mongodb://localhost:27017"
        const val DB_BLOCK = "block"
        const val DB_TRANSACTION = "transaction"
        const val DB_UTXO = "unspent_transaction"
    }

    private val connectionString = ConnectionString(dbUri)
    private val client = MongoClient.create(connectionString)
    private val database: MongoDatabase

    private lateinit var blockCollection: MongoCollection<Block>
    private lateinit var transactionCollection: MongoCollection<Transaction>
    private lateinit var utxoCollection: MongoCollection<Transaction>

    init {
        database = client.getDatabase(dataBaseName)
    }

    suspend fun initCollection() {
        database.createCollection(DB_BLOCK)
        database.createCollection(DB_TRANSACTION)
        database.createCollection(DB_UTXO)

        blockCollection = database.getCollection(DB_BLOCK)
        transactionCollection = database.getCollection(DB_TRANSACTION)
        utxoCollection = database.getCollection(DB_UTXO)
    }

    suspend fun initSchema() {
        blockCollection.createIndex(Indexes.ascending("height"))
        blockCollection.createIndex(Indexes.text("hash"))

        transactionCollection.createIndex(Indexes.text("hash"))
        transactionCollection.createIndex(Indexes.text("sourceHash"))
    }

    fun get(): MongoClient {
        return client
    }
}