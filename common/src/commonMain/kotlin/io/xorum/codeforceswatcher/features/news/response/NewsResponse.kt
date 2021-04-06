package io.xorum.codeforceswatcher.features.news.response

import io.xorum.codeforceswatcher.features.news.models.News
import kotlinx.serialization.Serializable

@Serializable
internal data class NewsResponse (
        val news: List<News>
)