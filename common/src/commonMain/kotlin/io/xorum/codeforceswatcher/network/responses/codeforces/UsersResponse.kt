package io.xorum.codeforceswatcher.network.responses.codeforces

import io.xorum.codeforceswatcher.features.users.models.User
import kotlinx.serialization.Serializable

@Serializable
data class UsersResponse(
        val status: String,
        val result: List<User>? = null,
        val comment: String? = null
)