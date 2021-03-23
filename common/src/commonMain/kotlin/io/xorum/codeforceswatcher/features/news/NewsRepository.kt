package io.xorum.codeforceswatcher.features.news

import io.ktor.client.request.*
import io.xorum.codeforceswatcher.network.responses.backend.HttpClientFactory
import io.xorum.codeforceswatcher.network.responses.backend.NewsResponse
import io.xorum.codeforceswatcher.redux.store
import io.xorum.codeforceswatcher.network.responses.backend.request

internal class NewsRepository {

    private val ktorResponseClient = HttpClientFactory().create(store.state.auth.token)

    suspend fun getNews() = request {
        ktorResponseClient.get<NewsResponse>(path = "news") {
            parameter("version", "v2")
        }
    }
}