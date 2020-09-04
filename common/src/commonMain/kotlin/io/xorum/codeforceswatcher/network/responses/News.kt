package io.xorum.codeforceswatcher.network.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class News {

    @Serializable
    @SerialName("PinnedPost")
    data class PinnedPost(
            val title: String,
            val link: String
    ) : News()

    @Serializable
    @SerialName("Post")
    data class Post(
            val id: Long,
            val createdAt: Long,
            val title: String,
            val content: String?,
            val author: User,
            val link: String
    ) : News()

    @Serializable
    @SerialName("Comment")
    data class Comment(
            val id: Long,
            val createdAt: Long,
            val title: String,
            val content: String,
            val author: User,
            val link: String
    ) : News()

    @Serializable
    data class User(
            val handle: String,
            val rank: String?,
            val avatar: String
    )
}
