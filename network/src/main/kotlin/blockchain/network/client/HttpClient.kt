package blockchain.network.client

import blockchain.network.core.PeerService
import blockchain.network.core.WalletService
import blockchain.utility.Log
import com.google.gson.Gson
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class HttpClient(baseUrl: String) {
    private val log = Log.get(this)

    companion object {
        const val TIMEOUT: Long = 1 // Second

        val okhttpClient = OkHttpClient.Builder()
            .connectionPool(ConnectionPool(10, 10, TimeUnit.MINUTES))
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okhttpClient)
        .build()

    val peerService: PeerService = retrofit.create(PeerService::class.java)
    val walletService = retrofit.create(WalletService::class.java)
    fun test() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://localhost:7070")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val emptyService = retrofit.create(EmptyService::class.java)
        val gson = Gson()
        log.info(gson.toJson(emptyService.hello().execute().body()))
    }
}