package io.xorum.codeforceswatcher.network

import io.ktor.client.request.parameter
import io.xorum.codeforceswatcher.features.auth.UserAccount
import io.xorum.codeforceswatcher.features.users.models.User
import io.xorum.codeforceswatcher.features.verification.VerificationCodeResponse
import io.xorum.codeforceswatcher.network.responses.backend.KtorResponseClient
import io.xorum.codeforceswatcher.network.responses.backend.NewsResponse
import io.xorum.codeforceswatcher.redux.store

internal class BackendRepository {

    private val ktorResponseClient
        get() = KtorResponseClient(token)

    suspend fun getNews(lang: String) =
            ktorResponseClient.get<NewsResponse>(path = "news") {
                parameter("lang", lang)
                parameter("version", "v2")
            }

    suspend fun fetchUsers(handles: String, isAllRatingChangesNeeded: Boolean) =
            ktorResponseClient.get<List<User>>("users") {
                parameter("handles", handles)
                parameter("isAllRatingChangesNeeded", isAllRatingChangesNeeded.toString())
            }

    private val token: String
        get() = store.state.auth.token.orEmpty()
}