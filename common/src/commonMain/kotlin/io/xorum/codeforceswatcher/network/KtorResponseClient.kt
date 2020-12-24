package io.xorum.codeforceswatcher.network

import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import io.xorum.codeforceswatcher.network.responses.Response
import io.xorum.codeforceswatcher.redux.analyticsController
import io.xorum.codeforceswatcher.util.stringify
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class KtorResponseClient {

    val backendApiClient by lazy { makeBackendApiClient() }

    suspend inline fun <reified T> post(
            path: String,
            block: HttpRequestBuilder.() -> Unit = {}
    ): Response<T>? {
        return try {
            Response.Success(result = backendApiClient.post(path = path, block = block))
        } catch (clientRequestException: ClientRequestException) {
            Response.Failure(getError(clientRequestException.response.content).error)
        } catch (e: Throwable) {
            analyticsController.logError(e.stringify())

            Response.Failure(null)
        }
    }

    suspend fun getError(responseContent: ByteReadChannel): Error {
        responseContent.readUTF8Line()?.let {
            return Json(JsonConfiguration.Stable).parse(Error.serializer(), it)
        }
        return Error("hello")
    }

    @Serializable
    data class Error(val error: String?)
}