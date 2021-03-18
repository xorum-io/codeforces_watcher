package io.xorum.codeforceswatcher.network.responses.backend

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import io.xorum.codeforceswatcher.redux.getLang
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val BACKEND_PROD_LINK = "algoris-me-backend.herokuapp.com"
const val BACKEND_STAGING_LINK = "algoris-me-backend-staging.herokuapp.com"

lateinit var backendLink: String

internal class HttpClientFactory {

    fun create(token: String?): HttpClient = HttpClient {
        defaultRequest {
            url {
                host = backendLink
                protocol = URLProtocol.HTTPS
            }

            header("Authorization", "Bearer $token")
            header("lang", getLang())
        }
        Json {
            serializer = KotlinxSerializer(
                    Json(from = Json) {
                        isLenient = true
                        ignoreUnknownKeys = true
                        allowSpecialFloatingPointValues = true
                        useArrayPolymorphism = true
                        encodeDefaults = true
                    }
            )
        }
        Logging {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }
}

suspend fun getError(responseContent: ByteReadChannel) =
        responseContent.readUTF8Line()?.let {
            Json(from = Json.Default) {}.decodeFromString(Error.serializer(), it)
        }

@Serializable
data class Error(val error: String?)

sealed class Response<T> {

    data class Success<T>(
            val result: T
    ) : Response<T>()

    data class Failure<T>(
            val error: String?
    ) : Response<T>()
}

suspend inline fun <T> request(block: () -> T) = try {
    Response.Success(block())
} catch (clientRequestException: ClientRequestException) {
    println(clientRequestException)
    Response.Failure(getError(clientRequestException.response.content)?.error)
} catch (e: Exception) {
    println(e)
    Response.Failure(null)
}
