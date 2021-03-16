package io.xorum.codeforceswatcher.network.responses.backend

import io.xorum.codeforceswatcher.features.auth.UserAccount
import io.xorum.codeforceswatcher.features.users.models.User
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
        val users: List<User>,
        val userAccount: UserAccount
)
