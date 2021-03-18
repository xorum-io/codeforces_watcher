package io.xorum.codeforceswatcher.network

import io.ktor.client.request.parameter
import io.xorum.codeforceswatcher.features.users.models.User
import io.xorum.codeforceswatcher.network.responses.backend.KtorResponseClient
import io.xorum.codeforceswatcher.network.responses.backend.NewsResponse
import io.xorum.codeforceswatcher.redux.store

internal class BackendRepository {

    private val ktorResponseClient = KtorResponseClient(store.state.auth.token)

    suspend fun getNews(lang: String) =
            ktorResponseClient.get<NewsResponse>(path = "news") {
                parameter("lang", lang)
                parameter("version", "v2")
            }

    suspend fun fetchUser(handle: String, isAllRatingChangesNeeded: Boolean) =
            ktorResponseClient.get<List<User>>("users") {
                parameter("handles", handle)
                parameter("isAllRatingChangesNeeded", isAllRatingChangesNeeded.toString())
            }
}