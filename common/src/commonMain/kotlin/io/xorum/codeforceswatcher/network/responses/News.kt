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
            val id: String,
            val modifiedAt: Long,
            val isModified: Boolean,
            val title: String,
            val content: String,
            val author: User,
            val link: String
    ) : News()

    @Serializable
    @SerialName("PostWithComment")
    data class PostWithComment(
            val post: Post,
            val comment: Comment
    ) : News()

    @Serializable
    data class Comment(
            val id: String,
            val createdAt: Long,
            val content: String,
            val author: User,
            val link: String
    )

    @Serializable
    data class User(
            val handle: String,
            val rank: String?,
            val avatar: String
    )
}
