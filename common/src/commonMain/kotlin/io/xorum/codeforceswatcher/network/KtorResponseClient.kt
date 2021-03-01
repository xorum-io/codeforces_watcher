package io.xorum.codeforceswatcher.network

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import io.xorum.codeforceswatcher.network.responses.Response
import io.xorum.codeforceswatcher.redux.analyticsController
import io.xorum.codeforceswatcher.redux.getLang
import io.xorum.codeforceswatcher.redux.store
import io.xorum.codeforceswatcher.util.stringify
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val BACKEND_PROD_LINK = "algoris-me-backend.herokuapp.com"
const val BACKEND_STAGING_LINK = "algoris-me-backend-staging.herokuapp.com"

lateinit var backendLink: String

class KtorResponseClient {

    val backendApiClient = makeBackendApiClient()

    suspend inline fun <reified T> post(
            path: String,
            block: HttpRequestBuilder.() -> Unit = {}
    ): Response<T> {
        return try {
            Response.Success(result = backendApiClient.post(path = path, block = block))
        } catch (clientRequestException: ClientRequestException) {
            Response.Failure(getError(clientRequestException.response.content)?.error)
        } catch (e: Throwable) {
            analyticsController.logError(e.stringify())

            Response.Failure(null)
        }
    }

    suspend inline fun <reified T> get(
            path: String,
            block: HttpRequestBuilder.() -> Unit = {}
    ): Response<T> {
        return try {
            Response.Success(result = backendApiClient.get(path = path, block = block))
        } catch (clientRequestException: ClientRequestException) {
            Response.Failure(getError(clientRequestException.response.content)?.error)
        } catch (e: Throwable) {
            analyticsController.logError(e.stringify())

            Response.Failure(null)
        }
    }

    suspend inline fun <reified T> put(
            path: String,
            block: HttpRequestBuilder.() -> Unit = {}
    ): Response<T> {
        return try {
            Response.Success(result = backendApiClient.put(path = path, block = block))
        } catch (clientRequestException: ClientRequestException) {
            Response.Failure(getError(clientRequestException.response.content)?.error)
        } catch (e: Throwable) {
            analyticsController.logError(e.stringify())

            Response.Failure(null)
        }
    }

    suspend fun getError(responseContent: ByteReadChannel) =
            responseContent.readUTF8Line()?.let {
                Json(from = Json.Default) {}.decodeFromString(Error.serializer(), it)
            }

    @Serializable
    data class Error(val error: String?)
}

internal fun makeBackendApiClient(): HttpClient = HttpClient {
    defaultRequest {
        url {
            host = backendLink
            protocol = URLProtocol.HTTPS
            header("token", userToken)
            header("lang", getLang())
        }
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

private val userToken: String?
    get() = store.state.users.userAccount?.token
