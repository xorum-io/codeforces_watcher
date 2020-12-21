package io.xorum.codeforceswatcher.features.auth

import io.xorum.codeforceswatcher.features.users.models.User

data class UserAccount(val user: User?, val token: String)