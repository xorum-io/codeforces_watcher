package io.xorum.codeforceswatcher.network

import io.ktor.client.request.parameter
import io.xorum.codeforceswatcher.features.auth.UserAccount
import io.xorum.codeforceswatcher.features.verification.VerificationCodeResponse
import io.xorum.codeforceswatcher.network.responses.backend.KtorResponseClient
import io.xorum.codeforceswatcher.network.responses.backend.NewsResponse

internal class BackendRepository {

    private val ktorResponseClient
        get() = KtorResponseClient()

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