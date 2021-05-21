package io.xorum.codeforceswatcher.features.notifications

import io.ktor.client.request.*
import io.xorum.codeforceswatcher.network.responses.backend.request

internal class NotificationsRepository {

    suspend fun addPushToken(token: String) = request { httpClient ->
        httpClient.put<Unit>(path = "notifications/token") {
            parameter("token", token)
        }
    }

    suspend fun deletePushToken(token: String) = request { httpClient ->
        httpClient.delete<Unit>(path = "notifications/token") {
            parameter("token", token)
        }
    }
}