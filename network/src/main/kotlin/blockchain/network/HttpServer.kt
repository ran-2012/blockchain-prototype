package blockchain.network

import io.javalin.Javalin

class HttpServer {

    fun start(){
        val app = Javalin.create(/*config*/)
            .get("/") { ctx ->
                ctx.result("Hello World")
            }
            .start(7070)
    }
}