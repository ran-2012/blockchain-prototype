package blockchain.storage

import com.mongodb.ConnectionString
import com.mongodb.kotlin.client.coroutine.MongoClient
import kotlinx.coroutines.*

class DataBaseClient(dataBaseName: String, dbUri: String = DEFAULT_DB_URI) {

    companion object {
        const val DEFAULT_DB_URI = "mongodb://localhost:27017"
    }

    private val connectionString = ConnectionString(dbUri)
    private val client = MongoClient.create(connectionString)

    init {
        val database = client.getDatabase(dataBaseName)
        runBlocking {
            database.createCollection("test")
        }
    }

}