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
import io.xorum.codeforceswatcher.network.responses.PinnedPost
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json.Default.nonstrict

private const val API_LINK = "5e80f1750eb3ec0016e917ff.mockapi.io/api/v1/pinned_post"

internal class PinnedPostsApiClient {

    private val pinnedPostApiClient = makePinnedPostApiClient()

    suspend fun getPinnedPost() = try {
        pinnedPostApiClient.get<PinnedPost>()
    } catch (t: Throwable) {
        null
    }

    @UseExperimental(UnstableDefault::class)
    private fun makePinnedPostApiClient(): HttpClient = HttpClient {
        expectSuccess = false
        defaultRequest {
            url {
                host = API_LINK
                protocol = URLProtocol.HTTPS
            }
        }
        Json {
            serializer = KotlinxSerializer(json = nonstrict)
        }
        Logging {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }
}
