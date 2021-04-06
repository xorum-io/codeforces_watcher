package io.xorum.codeforceswatcher.features.users.redux.requests

import io.xorum.codeforceswatcher.db.DatabaseQueries
import io.xorum.codeforceswatcher.features.auth.models.UserAccount
import io.xorum.codeforceswatcher.features.users.UsersRepository
import io.xorum.codeforceswatcher.features.users.models.User
import io.xorum.codeforceswatcher.network.responses.backend.Response
import io.xorum.codeforceswatcher.redux.*
import io.xorum.codeforceswatcher.util.UsersDiff
import tw.geothings.rekotlin.Action

enum class Source(val isToastNeeded: Boolean) {
    USER(true), BROADCAST(false), BACKGROUND(false)
}

class UsersRequests {

    class FetchUserData(
            private val users: List<User>,
            private val source: Source
    ) : Request() {

        private val usersRepository = UsersRepository()

        override suspend fun execute() {
            val result = when (val response = usersRepository.fetchUserData(getHandles(users))) {
                is Response.Success -> {
                    val (toAddDiff, toUpdateDiff, toDeleteDiff) = getDiff(response.result.users)
                    updateDatabaseUsers(toAddDiff, toUpdateDiff, toDeleteDiff)
                    val users = getOrderedUsers(toAddDiff, toDeleteDiff)
                    Success(users, response.result.userAccount, source)
                }
                is Response.Failure -> Failure(response.error.toMessage())
            }
            store.dispatch(result)
        }

        private fun getDiff(newUsers: List<User>): Triple<List<User>, List<User>, List<User>> {
            val allUsers = DatabaseQueries.Users.getAll()
            val (toAddDiff, toUpdateDiff) = UsersDiff(allUsers, newUsers).getDiff()
            val (toDeleteDiff, _) = UsersDiff(newUsers, allUsers).getDiff()

            return Triple(toAddDiff, toUpdateDiff, toDeleteDiff)
        }

        private fun updateDatabaseUsers(toAddDiff: List<User>, toUpdateDiff: List<User>, toDeleteDiff: List<User>) {
            DatabaseQueries.Users.delete(toDeleteDiff)
            DatabaseQueries.Users.update(toUpdateDiff)
            DatabaseQueries.Users.insert(toAddDiff)
        }

        private fun getOrderedUsers(toAddDiff: List<User>, toDeleteDiff: List<User>): List<User> {
            val usersMap = DatabaseQueries.Users.getAll().associateBy { it.handle }
            return store.state.users.users.map { usersMap[it.handle] ?: it }.minus(toDeleteDiff).plus(toAddDiff)
        }

        private fun getHandles(users: List<User>) = users.joinToString(separator = ",") { it.handle }

        data class Success(
                val users: List<User>,
                val userAccount: UserAccount?,
                val source: Source
        ) : Action

        data class Failure(override val message: Message) : ToastAction
    }

    class FetchUser(val handle: String) : Request() {

        private val usersRepository = UsersRepository()

        override suspend fun execute() {
            val profileUser = store.state.users.userAccount?.codeforcesUser

            if (handle == profileUser?.handle) {
                store.dispatch(Success(profileUser))
            } else {
                val result = when (val response = usersRepository.fetchUser(handle)) {
                    is Response.Success -> {
                        val user = response.result
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

        private val usersRepository = UsersRepository()

        override suspend fun execute() {
            val result = when (val response = usersRepository.deleteUser(user.handle)) {
                is Response.Success -> {
                    DatabaseQueries.Users.delete(user.handle)
                    Success(user)
                }
                is Response.Failure -> Failure(response.error.toMessage())
            }
            store.dispatch(result)
        }

        data class Success(val user: User) : Action
        data class Failure(override val message: Message) : ToastAction
    }

    class AddUser(private val handle: String) : Request() {

        private val usersRepository = UsersRepository()

        override suspend fun execute() {
            if (DatabaseQueries.Users.getAll().find { it.handle.equals(handle, ignoreCase = true) } != null) {
                store.dispatch(Failure(Message.UserAlreadyAdded))
                return
            }
            val result = when (val response = usersRepository.addUser(handle)) {
                is Response.Success -> {
                    val user = response.result
                    DatabaseQueries.Users.insert(user)
                    Success(user)
                }
                is Response.Failure -> Failure(response.error.toMessage())
            }
            store.dispatch(result)
        }

        data class Success(val user: User) : Action

        data class Failure(override val message: Message) : ToastAction
    }

    class Destroy : Request() {

        override suspend fun execute() {
            DatabaseQueries.Users.deleteAll()
        }
    }
}
