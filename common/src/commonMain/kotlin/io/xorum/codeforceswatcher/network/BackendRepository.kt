package io.xorum.codeforceswatcher.network

import io.ktor.client.request.*
import io.xorum.codeforceswatcher.features.users.models.User
import io.xorum.codeforceswatcher.network.responses.backend.HttpClientFactory
import io.xorum.codeforceswatcher.network.responses.backend.NewsResponse
import io.xorum.codeforceswatcher.redux.store
import io.xorum.codeforceswatcher.network.responses.backend.request

internal class BackendRepository {

    private val ktorResponseClient = HttpClientFactory().create(store.state.auth.token)

    suspend fun getNews() = request {
        ktorResponseClient.get<NewsResponse>(path = "news") {
            parameter("version", "v2")
        }
    }

    suspend fun fetchUser(handle: String, isAllRatingChangesNeeded: Boolean) = request {
        ktorResponseClient.get<List<User>>("users") {
            parameter("handles", handle)
            parameter("isAllRatingChangesNeeded", isAllRatingChangesNeeded.toString())
        }
    }
}