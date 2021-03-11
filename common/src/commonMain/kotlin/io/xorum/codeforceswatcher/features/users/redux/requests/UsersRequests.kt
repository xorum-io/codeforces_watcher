package io.xorum.codeforceswatcher.features.users.redux.requests

import io.xorum.codeforceswatcher.db.DatabaseQueries
import io.xorum.codeforceswatcher.features.users.models.User
import io.xorum.codeforceswatcher.network.responses.backend.Response
import io.xorum.codeforceswatcher.redux.*
import io.xorum.codeforceswatcher.util.AnalyticsEvents
import io.xorum.codeforceswatcher.util.UsersDiff
import tw.geothings.rekotlin.Action

enum class Source(val isToastNeeded: Boolean) {
    USER(true), BROADCAST(false), BACKGROUND(false)
}

class UsersRequests {

    class FetchUsers(private val source: Source) : Request() {

        override suspend fun execute() {
            val users = store.state.users.users
            val allUsersResponse = backendRepository.fetchUsers(getHandles(users), isAllRatingChangesNeeded = false)
            val userAccountCfUserResponse = backendRepository.fetchUsers(
                    getHandles(listOfNotNull(store.state.users.userAccount?.codeforcesUser)),
                    isAllRatingChangesNeeded = true
            )

            val result = when {
                allUsersResponse is Response.Failure -> Failure(allUsersResponse.error.toMessage())
                userAccountCfUserResponse is Response.Failure -> Failure(userAccountCfUserResponse.error.toMessage())
                allUsersResponse is Response.Success && userAccountCfUserResponse is Response.Success -> {
                    saveUsers(allUsersResponse.result)
                    val newUsers = DatabaseQueries.Users.getAll()
                    Success(newUsers, userAccountCfUserResponse.result.firstOrNull(), source)
                }
                else -> throw IllegalStateException()
            }
            store.dispatch(result)
        }

        private fun saveUsers(newUsers: List<User>) {
            val allUsers = DatabaseQueries.Users.getAll()
            val (diff, toUpdateDiff) = UsersDiff(allUsers, newUsers).getDiff()

            DatabaseQueries.Users.update(toUpdateDiff)
            DatabaseQueries.Users.insert(diff)
        }

        private fun getHandles(users: List<User>) = users.joinToString(separator = ",") { it.handle }

        data class Success(
                val users: List<User>,
                val userAccountCfUser: User?,
                val source: Source
        ) : Action

        data class Failure(override val message: Message) : ToastAction
    }

    class FetchUser(val handle: String) : Request() {
        override suspend fun execute() {
            val profileUser = store.state.users.userAccount?.codeforcesUser

            if (handle == profileUser?.handle) {
                store.dispatch(Success(profileUser))
            } else {
                val result = when (val response = backendRepository.fetchUsers(handle, isAllRatingChangesNeeded = true)) {
                    is Response.Success -> {
                        val user = response.result.first()
                        DatabaseQueries.Users.update(user)
                        Success(user)
                    }
                    is Response.Failure -> Success(DatabaseQueries.Users.get(handle))
                }
                store.dispatch(result)
            }
        }

        data class Success(val user: User) : Action
    }

    class DeleteUser(val user: User) : Request() {

        override suspend fun execute() {
            DatabaseQueries.Users.delete(user.handle)
        }
    }

    class AddUser(private val handle: String) : Request() {

        override suspend fun execute() {
            when (val response = backendRepository.fetchUsers(handle, isAllRatingChangesNeeded = false)) {
                is Response.Success -> response.result.firstOrNull()?.let { user -> addUser(user) }
                        ?: store.dispatch(Failure(null.toMessage()))
                is Response.Failure -> store.dispatch(Failure(response.error.toMessage()))
            }
        }

        private fun addUser(user: User) {
            val foundUser = DatabaseQueries.Users.getAll()
                    .find { currentUser -> currentUser.handle == user.handle }

            if (foundUser == null) {
                DatabaseQueries.Users.insert(user)
                store.dispatch(Success(user))
                analyticsController.logEvent(AnalyticsEvents.USER_ADDED)
            } else {
                store.dispatch(Failure(Message.UserAlreadyAdded))
            }
        }

        data class Success(val user: User) : Action

        data class Failure(override val message: Message) : ToastAction
    }

    object ClearCurrentUser : Action

    class Destroy : Request() {

        override suspend fun execute() {
            DatabaseQueries.Users.deleteAll()
        }
    }
}
