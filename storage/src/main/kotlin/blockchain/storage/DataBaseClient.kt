package blockchain.storage

import blockchain.data.core.Block
import blockchain.data.core.Transaction
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.model.Indexes
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider

class DataBaseClient(dataBaseName: String, private val global: Boolean = false, dbUri: String = DEFAULT_DB_URI) {

    companion object {
        const val DEFAULT_DB_URI = "mongodb://localhost:27017"
        const val DB_BLOCK = "block"
        const val DB_GLOBAL_BLOCK = "global_block"
        const val DB_TRANSACTION = "transaction"
        const val DB_GLOBAL_TRANSACTION = "global_transaction"
        const val DB_UTXO = "unspent_transaction"

        const val FIELD_HEIGHT = "height"
        const val FIELD_HASH = "hash"
        const val FIELD_SOURCE_ADDRESS = "sourceAddress"
    }

    private val connectionString = ConnectionString(dbUri)
    private val client: MongoClient

    init {
        val pojoCodecRegistry = CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
        val codecRegistry: CodecRegistry = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            pojoCodecRegistry
        )
        val clientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .codecRegistry(codecRegistry)
            .build()
        client = MongoClient.create(clientSettings)
    }

    private val database: MongoDatabase

    private lateinit var blockCollection: MongoCollection<Block>
    private lateinit var transactionCollection: MongoCollection<Transaction>

    init {
        database = client.getDatabase(dataBaseName)
    }

    suspend fun initCollection() {
        database.createCollection(DB_BLOCK)
        database.createCollection(DB_GLOBAL_BLOCK)
        database.createCollection(DB_GLOBAL_TRANSACTION)
        database.createCollection(DB_GLOBAL_TRANSACTION)

        blockCollection = if (!global) {
            database.getCollection(DB_BLOCK)
        } else {
            database.getCollection(DB_GLOBAL_BLOCK)
        }
        transactionCollection = if (!global) {
            database.getCollection(DB_TRANSACTION)
        } else {
            database.getCollection(DB_GLOBAL_TRANSACTION)
        }
    }

    suspend fun initSchema() {
        blockCollection.createIndex(Indexes.ascending(FIELD_HEIGHT))
        blockCollection.createIndex(Indexes.text(FIELD_HASH))

        transactionCollection.createIndex(
            Indexes.compoundIndex(
                Indexes.text(FIELD_SOURCE_ADDRESS),
                Indexes.text(FIELD_HASH)
            )
        )
    }

    fun get(): MongoClient {
        return client
    }

    fun block(): MongoCollection<Block> {
        return blockCollection
    }

    fun transaction(): MongoCollection<Transaction> {
        return transactionCollection
    }
}