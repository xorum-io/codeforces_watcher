package io.xorum.codeforceswatcher.features.auth

import io.xorum.codeforceswatcher.features.users.models.User
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class UserAccount(
        val codeforcesUser: User? = null,
        val email: String,
        val token: String
) {

    fun toJson(): String {
        val serializer = Json(from = Json.Default) { ignoreUnknownKeys = true }
        return serializer.encodeToString(serializer(), this)
    }

    companion object {

        fun fromJson(json: String): UserAccount {
            val serializer = Json(from = Json.Default) { ignoreUnknownKeys = true }
            return serializer.decodeFromString(serializer(), json)
        }
    }
}