package io.xorum.codeforceswatcher.network.responses.backend

import io.xorum.codeforceswatcher.features.news.News
import kotlinx.serialization.Serializable

@Serializable
data class NewsResponse (
        val news: List<News>
)