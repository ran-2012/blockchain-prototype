package blockchain.network.client

import blockchain.network.core.PeerService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class HttpClient(baseUrl: String) {

    companion object {
        const val TIMEOUT: Long = 1 // Second

        val okhttpClient = OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okhttpClient)
        .build()

    val peerService: PeerService = retrofit.create(PeerService::class.java)
}