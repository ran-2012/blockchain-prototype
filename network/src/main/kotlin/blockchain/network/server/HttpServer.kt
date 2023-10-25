package blockchain.network.server

import blockchain.network.client.EmptyService
import blockchain.network.SampleData
import blockchain.utility.Log
import com.google.gson.Gson
import io.javalin.Javalin
import io.javalin.json.JavalinGson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HttpServer {

    private val log = Log.get(this);

    init {
    }

    fun start() {
        log.info("Start server")
        val app = Javalin
            .create { config ->
                config.requestLogger.http { ctx, ms ->
                    log.debug("Request received, url: {}", ctx.url())
                }
                config.jsonMapper(JavalinGson())
            }
            .events { event ->
                event.serverStarted {
                    log.info("Server started")
                    val retrofit = Retrofit.Builder()
                        .baseUrl("http://localhost:7070")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    val emptyService = retrofit.create(EmptyService::class.java)
                    val gson = Gson();
                    log.info(gson.toJson(emptyService.hello().execute().body()))
                }
            }
            .get("/") { ctx ->
                ctx.json(SampleData())
            }
            .start(7070)

    }
}