package io.xorum.codeforceswatcher.network.responses

import kotlinx.serialization.Serializable

@Serializable
data class NewsResponse (
        val news: List<News>
)