package com.bogdan.codeforceswatcher.features.users.redux.requests

import com.bogdan.codeforceswatcher.CwApp
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.features.users.models.User
import com.bogdan.codeforceswatcher.network.models.Error
import com.bogdan.codeforceswatcher.network.getUsers
import com.bogdan.codeforceswatcher.network.models.UsersRequestResult
import com.bogdan.codeforceswatcher.redux.Request
import com.bogdan.codeforceswatcher.redux.actions.ToastAction
import com.bogdan.codeforceswatcher.room.DatabaseClient
import com.bogdan.codeforceswatcher.store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.rekotlin.Action

enum class Source(val isToastNeeded: Boolean) {
    USER(true), BROADCAST(false), BACKGROUND(false)
}

class UsersRequests {

    class FetchUsers(
        private val source: Source
    ) : Request() {

        override fun execute() {
            val users: List<User> = DatabaseClient.userDao.getAll()
            getUsers(getHandles(users), true) { result ->
                println("getUsers: status : ${store.state.users.status}")
                println("getUsers: result : $result")
                //GlobalScope.launch(Dispatchers.Main) {
                    when (result) {
                        is UsersRequestResult.Failure -> dispatchError(result.error)
                        is UsersRequestResult.Success ->
                            store.dispatch(
                                Success(result.users, getDifferenceAndUpdate(users, result.users), source)
                            )
                    }
                //}
            }
        }

        private fun dispatchError(error: Error) {
            println("dispatch : error")
            val noConnectionError = CwApp.app.resources.getString(R.string.no_connection)
            val fetchingUsersError = CwApp.app.resources.getString(R.string.failed_to_fetch_users)

            when (error) {
                Error.INTERNET ->
                    store.dispatch(
                        Failure(if (source.isToastNeeded) noConnectionError else null)
                    )
                Error.RESPONSE ->
                    store.dispatch(
                        Failure(if (source.isToastNeeded) fetchingUsersError else null)
                    )
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
                    DatabaseClient.userDao.update(user)
                }
            }
            return difference
        }

        private fun getHandles(roomUserList: List<User>): String {
            var handles = ""
            for (element in roomUserList) {
                handles += element.handle + ";"
            }
            return handles
        }

        data class Success(
            val users: List<User>,
            val notificationData: List<Pair<String, Int>>,
            val source: Source
        ) : Action

        data class Failure(override val message: String?) : ToastAction
    }

}