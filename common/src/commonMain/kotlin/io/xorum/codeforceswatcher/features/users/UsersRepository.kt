package io.xorum.codeforceswatcher.features.users

import io.ktor.client.request.*
import io.xorum.codeforceswatcher.features.users.models.User
import io.xorum.codeforceswatcher.network.responses.backend.HttpClientFactory
import io.xorum.codeforceswatcher.network.responses.backend.UserData
import io.xorum.codeforceswatcher.network.responses.backend.request

internal class UsersRepository(token: String?) {

    private val ktorResponseClient = HttpClientFactory().create(token)

    suspend fun fetchUserData(handles: String) = request {
        ktorResponseClient.get<UserData>("user/data") {
            parameter("handles", handles)
        }
    }

    suspend fun fetchUser(handle: String, isAllRatingChangesNeeded: Boolean) = request {
        ktorResponseClient.get<List<User>>("users") {
            parameter("handles", handle)
            parameter("isAllRatingChangesNeeded", isAllRatingChangesNeeded.toString())
        }
    }

    suspend fun addUser(handle: String) = request {
        ktorResponseClient.post<User>("user/add") {
            parameter("handle", handle)
        }
    }

    suspend fun deleteUser(handle: String) = request {
        ktorResponseClient.post<Unit>("user/delete") {
            parameter("handle", handle)
        }
    }
}