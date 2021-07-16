package io.xorum.codeforceswatcher.util

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import io.xorum.codeforceswatcher.redux.firebaseController
import io.xorum.codeforceswatcher.redux.getLang
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

            token?.let { header("Authorization", "Bearer $token") }
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

internal suspend fun getError(responseContent: ByteReadChannel) =
        responseContent.readUTF8Line()?.let {
            Json(from = Json.Default) {}.decodeFromString(Error.serializer(), it)
        }

@Serializable
internal data class Error(val error: String?)

internal suspend inline fun <T> request(block: (httpClient: HttpClient) -> T) = try {
    val (token, exception) = suspendCoroutine<Pair<String?, Exception?>> { continuation ->
        firebaseController.fetchToken { token, exception ->
            continuation.resume(Pair(token, exception))
        }
    }
    exception?.let { throw it }
    val httpClient = HttpClientFactory().create(token)
    Response.Success(block(httpClient))
} catch (clientRequestException: ClientRequestException) {
    println(clientRequestException)
    Response.Failure(getError(clientRequestException.response.content)?.error)
} catch (t: Throwable) {
    println(t)
    Response.Failure(null)
}

internal sealed class Response<T> {

    data class Success<T>(
            val result: T
    ) : Response<T>()

    data class Failure<T>(
            val error: String?
    ) : Response<T>()
}