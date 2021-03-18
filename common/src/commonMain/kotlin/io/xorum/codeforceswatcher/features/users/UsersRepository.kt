package io.xorum.codeforceswatcher.features.users

import io.ktor.client.request.*
import io.xorum.codeforceswatcher.network.responses.backend.HttpClientFactory
import io.xorum.codeforceswatcher.network.responses.backend.UserData
import io.xorum.codeforceswatcher.network.responses.backend.request

internal class UsersRepository(token: String?) {

    private val ktorResponseClient = HttpClientFactory().create(token)

    suspend fun fetchUsersData(handles: String) = request {
        ktorResponseClient.get<UserData>("user/fetch-user-data") {
            parameter("handles", handles)
        }
    }
}