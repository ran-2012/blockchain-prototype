package blockchain.network.client

import blockchain.network.SampleData
import retrofit2.Call
import retrofit2.http.GET

interface EmptyService {

    @GET("/")
    fun hello(): Call<SampleData>
}