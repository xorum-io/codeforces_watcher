package io.xorum.codeforceswatcher.features.notifications.redux

import io.xorum.codeforceswatcher.features.notifications.NotificationsRepository
import io.xorum.codeforceswatcher.redux.Request
import io.xorum.codeforceswatcher.redux.pushToken

class NotificationsRequests {

    object AddPushToken : Request() {

        private val notificationsRepository = NotificationsRepository()

        override suspend fun execute() {
            pushToken?.let {
                notificationsRepository.addPushToken(it)
            }
        }
    }
}