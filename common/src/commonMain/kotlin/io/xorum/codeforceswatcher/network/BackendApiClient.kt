package io.xorum.codeforceswatcher.network

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.xorum.codeforceswatcher.network.responses.NewsResponse
import io.xorum.codeforceswatcher.redux.analyticsController
import kotlinx.serialization.UnstableDefault

internal class BackendApiClient {

    private val makeBackendApiClient = makeBackendApiClient()

    suspend fun getNews(lang: String) = try {
        makeBackendApiClient.get<NewsResponse>(path = "news") {
            parameter("lang", lang)
        }
    } catch (t: Throwable) {
        analyticsController.logFetchNewsFailure()
        println(t)
        null
    }

    @UseExperimental(UnstableDefault::class)
    private fun makeBackendApiClient(): HttpClient = HttpClient {
        expectSuccess = false
        defaultRequest {
            url {
                host = API_LINK
                protocol = URLProtocol.HTTPS
            }
        }
        Json {
            serializer = KotlinxSerializer(json = kotlinx.serialization.json.Json.nonstrict)
        }
        Logging {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }

    companion object {
        private const val API_LINK = "algoris-me-backend.herokuapp.com"
    }
}