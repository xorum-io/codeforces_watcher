package io.xorum.codeforceswatcher.features.users

import io.ktor.client.request.*
import io.xorum.codeforceswatcher.network.responses.backend.KtorResponseClient
import io.xorum.codeforceswatcher.network.responses.backend.UserData

internal class UsersRepository(
        private val token: String?
) {

    private val ktorResponseClient
        get() = KtorResponseClient(token)

    suspend fun fetchUsersData(handles: String) = ktorResponseClient.get<UserData>("user/fetch-user-data") {
        parameter("handles", handles)
    }
}