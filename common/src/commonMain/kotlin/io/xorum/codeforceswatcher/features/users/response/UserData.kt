package io.xorum.codeforceswatcher.features.users.response

import io.xorum.codeforceswatcher.features.auth.models.UserAccount
import io.xorum.codeforceswatcher.features.users.models.User
import kotlinx.serialization.Serializable

@Serializable
internal data class UserData(
        val users: List<User>,
        val userAccount: UserAccount?
)
