package io.xorum.codeforceswatcher.features.auth

import io.xorum.codeforceswatcher.features.auth.redux.AuthState
import io.xorum.codeforceswatcher.features.users.models.User
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class UserAccount(
        val codeforcesUser: User? = null
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

fun UserAccount?.getAuthStage() = when {
    this?.codeforcesUser != null -> AuthState.Stage.VERIFIED
    this != null -> AuthState.Stage.SIGNED_IN
    else -> AuthState.Stage.NOT_SIGNED_IN
}