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
import io.ktor.client.request.parameter
import io.ktor.http.URLProtocol
import io.xorum.codeforceswatcher.features.auth.UserAccount
import io.xorum.codeforceswatcher.network.responses.NewsResponse
import io.xorum.codeforceswatcher.redux.analyticsController
import io.xorum.codeforceswatcher.util.AnalyticsEvents
import kotlinx.serialization.UnstableDefault

const val BACKEND_PROD_LINK = "algoris-me-backend.herokuapp.com"
const val BACKEND_STAGING_LINK = "algoris-me-backend-staging.herokuapp.com"

lateinit var backendLink: String

internal class BackendRepository {

    private val backendApiClient by lazy { makeBackendApiClient() }
    private val ktorResponseClient = KtorResponseClient()

    suspend fun getNews(lang: String) = try {
        backendApiClient.get<NewsResponse>(path = "news") {
            parameter("lang", lang)
            parameter("version", "v2")
        }
    } catch (e: Throwable) {
        analyticsController.logEvent(AnalyticsEvents.NEWS_FETCH_FAILURE)
        null
    }

    suspend fun signIn(email: String, password: String) =
            ktorResponseClient.post<UserAccount>("user/sign-in") {
                parameter("email", email)
                parameter("password", password)
            }
}

@UseExperimental(UnstableDefault::class)
internal fun makeBackendApiClient(): HttpClient = HttpClient {
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

