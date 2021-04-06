package io.xorum.codeforceswatcher.features.news

import io.ktor.client.request.*
import io.xorum.codeforceswatcher.features.news.response.NewsResponse
import io.xorum.codeforceswatcher.network.responses.backend.request

internal class NewsRepository {

    suspend fun getNews() = request { httpClient ->
        httpClient.get<NewsResponse>(path = "news") {
            parameter("version", "v2")
        }
    }
}