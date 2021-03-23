package io.xorum.codeforceswatcher.features.news.models

import kotlinx.serialization.Serializable

@Serializable
data class NewsResponse (
        val news: List<News>
)