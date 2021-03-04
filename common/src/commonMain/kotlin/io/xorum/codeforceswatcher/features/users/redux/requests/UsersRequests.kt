package io.xorum.codeforceswatcher.features.users.redux.requests

import io.xorum.codeforceswatcher.db.DatabaseQueries
import io.xorum.codeforceswatcher.features.auth.UserAccount
import io.xorum.codeforceswatcher.features.users.models.User
import io.xorum.codeforceswatcher.features.users.redux.getUsers
import io.xorum.codeforceswatcher.features.users.redux.models.UsersRequestResult
import io.xorum.codeforceswatcher.redux.*
import io.xorum.codeforceswatcher.util.AnalyticsEvents
import io.xorum.codeforceswatcher.util.defineLang
import io.xorum.codeforceswatcher.util.settings
import kotlinx.coroutines.delay
import tw.geothings.rekotlin.Action

enum class Source(val isToastNeeded: Boolean) {
    USER(true), BROADCAST(false), BACKGROUND(false)
}

class UsersRequests {

    class FetchUsers(
            private val source: Source,
            private val language: String
    ) : Request() {

        override suspend fun execute() {
            // Use this delay because actions, problems and contests requests managed to work out(and Codeforces didn't block them)
            if (source == Source.BACKGROUND) delay(1500)
            val users = store.state.users.users
            when (val result = getUsers(getHandles(users), true, lang = language.defineLang())) {
                is UsersRequestResult.Failure -> {
                    store.dispatch(Failure(if (source.isToastNeeded) result.error.message else Message.None))
                }
                is UsersRequestResult.Success -> {
                    store.dispatch(Success(result.users, getDifferenceAndUpdate(users, result.users), source))
                }
            }
        }

        private fun getDifferenceAndUpdate(users: List<User>, updatedUsers: List<User>): List<Pair<String, Int>> {
            val difference: MutableList<Pair<String, Int>> = mutableListOf()
            for (user in updatedUsers) {
                users.find { it.handle == user.handle }?.let { foundUser ->
                    user.id = foundUser.id
                    if (foundUser.ratingChanges != user.ratingChanges) {
                        user.ratingChanges.lastOrNull()?.let { ratingChange ->
                            val delta = ratingChange.newRating - ratingChange.oldRating
                            difference.add(Pair(user.handle, delta))
                        }
                    }
                }
            }
            DatabaseQueries.Users.insert(updatedUsers)
            return difference
        }

        private fun getHandles(users: List<User>) = users.joinToString(separator = ";") { it.handle }

        data class Success(
                val users: List<User>,
                val notificationData: List<Pair<String, Int>>,
                val source: Source
        ) : Action

        data class Failure(override val message: Message) : ToastAction
    }

    class DeleteUser(val user: User) : Request() {

        override suspend fun execute() {
            DatabaseQueries.Users.delete(user.id)
        }
    }

    class AddUser(
            private val handle: String,
            private val language: String
    ) : Request() {

        override suspend fun execute() {
            when (val result = getUsers(handle, true, lang = language.defineLang())) {
                is UsersRequestResult.Failure -> store.dispatch(Failure(result.error.message))
                is UsersRequestResult.Success -> result.users.firstOrNull()?.let { user -> addUser(user) }
            }
        }

        private fun addUser(user: User) {
            val foundUser = DatabaseQueries.Users.getAll()
                    .find { currentUser -> currentUser.handle == user.handle }

            if (foundUser == null) {
                user.id = DatabaseQueries.Users.insert(user)
                store.dispatch(Success(user))
                analyticsController.logEvent(AnalyticsEvents.USER_ADDED)
            } else {
                store.dispatch(Failure(Message.UserAlreadyAdded))
            }
        }

        data class Success(val user: User) : Action

        data class Failure(override val message: Message) : ToastAction
    }

    class Destroy : Request() {

        override suspend fun execute() {
            // TODO DatabaseQueries.Users.deleteAll()
        }
    }
}
