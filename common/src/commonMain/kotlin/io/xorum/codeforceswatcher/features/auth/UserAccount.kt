package io.xorum.codeforceswatcher.features.auth

import io.xorum.codeforceswatcher.features.users.models.User
import kotlinx.serialization.Serializable

@Serializable
data class UserAccount(
        val codeforcesUser: User? = null,
        val email: String,
        val token: String
)