package io.xorum.codeforceswatcher.network

import io.ktor.client.HttpClient
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.Json
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import io.xorum.codeforceswatcher.network.responses.ContestResponse
import io.xorum.codeforceswatcher.redux.analyticsController
import io.xorum.codeforceswatcher.util.stringify

private const val KONTESTS_API_LINK = "www.kontests.net/api/v1"

internal class KontestsRepository {

    private val kontestsApiClient = makeKontestsApiClient()

    suspend fun getAllContests() = try {
        kontestsApiClient.get<List<ContestResponse>>(path = "all")
    } catch (e: Throwable) {
        analyticsController.logError(e.stringify())
        e.printStackTrace()

        null
    }

    private fun makeKontestsApiClient(): HttpClient = HttpClient {
        expectSuccess = false
        defaultRequest {
            url {
                host = KONTESTS_API_LINK
                protocol = URLProtocol.HTTPS
            }
        }
        Json {
            serializer = KotlinxSerializer(
                kotlinx.serialization.json.Json(from = kotlinx.serialization.json.Json.Default) {
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
