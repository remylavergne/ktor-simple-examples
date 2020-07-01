package dev.remylavergne

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.client.HttpClient
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {
        }
    }

    install(DefaultHeaders)

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/getUserPost") {
            // Get a Post Dto from third API Typicode.com
            val url = "https://jsonplaceholder.typicode.com/posts/1"
            // Make client to handle deserialization
            val client = HttpClient() {
                install(JsonFeature) {
                    serializer = GsonSerializer()
                }
            }

            // Make API call
            val apiResponse = client.get<ApiUserPostDto>(url)

            // Transform my object to send only what I want to my client
            val responseForClient = apiResponse.getClientUserPost()

            call.respond(responseForClient)
        }
    }
}

data class ApiUserPostDto(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
) {
    fun getClientUserPost() = ClientUserPost(id)
}

/**
 * Data sent only to the client
 */
data class ClientUserPost(val id: Int)

