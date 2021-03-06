package io.xorum.codeforceswatcher.features.users.models

import io.xorum.codeforceswatcher.DbUser
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

@Serializable
data class User(
        val id: Long,
        val avatar: String,
        val rank: String?,
        val maxRank: String?,
        val handle: String,
        val rating: Int?,
        val maxRating: Int?,
        val firstName: String?,
        val lastName: String?,
        val ratingChanges: List<RatingChange> = listOf(),
        val contribution: Long?
) {

    companion object {

        fun fromDB(dbUser: DbUser): User {
            val serializer = Json(from = Json.Default) { ignoreUnknownKeys = true }
            return User(
                    id = dbUser.id,
                    avatar = dbUser.avatar,
                    rank = dbUser.rank,
                    maxRank = dbUser.maxRank,
                    handle = dbUser.handle,
                    rating = dbUser.rating?.toInt(),
                    maxRating = dbUser.maxRating?.toInt(),
                    firstName = dbUser.firstName,
                    lastName = dbUser.lastName,
                    ratingChanges = serializer.decodeFromString(ListSerializer(RatingChange.serializer()), dbUser.ratingChanges),
                    contribution = dbUser.contribution
            )
        }
    }
}