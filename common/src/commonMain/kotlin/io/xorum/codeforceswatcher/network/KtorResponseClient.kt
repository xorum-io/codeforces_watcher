package io.xorum.codeforceswatcher.network

import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import io.xorum.codeforceswatcher.network.responses.Response
import io.xorum.codeforceswatcher.redux.analyticsController
import io.xorum.codeforceswatcher.util.stringify
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class KtorResponseClient {

    val backendApiClient by lazy { makeBackendApiClient() }

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
            println(clientRequestException)
            Response.Failure(getError(clientRequestException.response.content)?.error)
        } catch (e: Throwable) {
            println(e)
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
            println(clientRequestException)
            Response.Failure(getError(clientRequestException.response.content)?.error)
        } catch (e: Throwable) {
            println(e)
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
