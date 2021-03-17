package io.xorum.codeforceswatcher.redux.middlewares

import io.xorum.codeforceswatcher.features.users.redux.requests.Source
import io.xorum.codeforceswatcher.features.users.redux.requests.UsersRequests
import tw.geothings.rekotlin.Middleware
import tw.geothings.rekotlin.StateType

interface NotificationHandler {

    fun handle(notificationData: List<Pair<String, Int>>)
}

var notificationHandler: NotificationHandler? = null

val notificationMiddleware: Middleware<StateType> = { _, _ ->
    { next ->
        { action ->
            when (action) {
                is UsersRequests.FetchUsersData.Success -> {
                    if (action.source == Source.BROADCAST) {
                        val notificationData = action.users.mapNotNull {
                            it.ratingChanges.lastOrNull()?.let { ratingChange ->
                                val delta = ratingChange.newRating - ratingChange.oldRating
                                Pair(it.handle, delta)
                            }
                        }
                        notificationHandler?.handle(notificationData)
                    }
                }
            }

            next(action)
        }
    }
}
