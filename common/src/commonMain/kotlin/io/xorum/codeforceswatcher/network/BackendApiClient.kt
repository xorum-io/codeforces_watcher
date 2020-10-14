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
import io.xorum.codeforceswatcher.util.stringify
import kotlinx.serialization.UnstableDefault

const val BACKEND_PROD_LINK = "algoris-me-backend.herokuapp.com"
const val BACKEND_STAGING_LINK = "algoris-me-backend-staging.herokuapp.com"

lateinit var backendLink: String

internal class BackendApiClient {

    private val makeBackendApiClient by lazy { makeBackendApiClient() }

    suspend fun getNews(lang: String) = try {
        makeBackendApiClient.get<NewsResponse>(path = "news") {
            parameter("lang", lang)
            parameter("version", "v1")
        }
    } catch (e: Exception) {
        analyticsController.logFetchNewsFailure()
        analyticsController.logError(e.stringify())
        e.printStackTrace()

        null
    }

    @UseExperimental(UnstableDefault::class)
    private fun makeBackendApiClient(): HttpClient = HttpClient {
        expectSuccess = false
        defaultRequest {
            url {
                host = backendLink
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
}