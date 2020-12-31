package io.xorum.codeforceswatcher.network

import io.ktor.client.HttpClient
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.Json
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.URLProtocol
import io.xorum.codeforceswatcher.features.auth.UserAccount
import io.xorum.codeforceswatcher.features.verification.VerificationCodeResponse
import io.xorum.codeforceswatcher.network.responses.NewsResponse
import io.xorum.codeforceswatcher.redux.store
import kotlinx.serialization.UnstableDefault

const val BACKEND_PROD_LINK = "algoris-me-backend.herokuapp.com"
const val BACKEND_STAGING_LINK = "algoris-me-backend-staging.herokuapp.com"

lateinit var backendLink: String

internal class BackendRepository {

    private val ktorResponseClient = KtorResponseClient()

    suspend fun getNews(lang: String) =
            ktorResponseClient.get<NewsResponse>(path = "news") {
                parameter("lang", lang)
                parameter("version", "v2")
            }

    suspend fun signIn(email: String, password: String) =
            ktorResponseClient.post<UserAccount>("user/sign-in") {
                parameter("email", email)
                parameter("password", password)
            }

    suspend fun signUp(email: String, password: String) =
            ktorResponseClient.post<UserAccount>("user/sign-up") {
                parameter("email", email)
                parameter("password", password)
            }

    suspend fun fetchCodeforcesVerificationCode() = ktorResponseClient.put<VerificationCodeResponse>("user/generate-verify-code/codeforces")
    suspend fun verifyCodeforcesAccount(handle: String) = ktorResponseClient.post<UserAccount>("user/verify/codeforces") {
        parameter("handle", handle)
    }
}

@UseExperimental(UnstableDefault::class)
internal fun makeBackendApiClient(): HttpClient = HttpClient {
    defaultRequest {
        url {
            host = backendLink
            protocol = URLProtocol.HTTPS
            header("token", userToken)
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


private val userToken: String?
    get() = store.state.users.userAccount?.token
