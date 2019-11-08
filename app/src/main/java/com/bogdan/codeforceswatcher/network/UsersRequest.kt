package com.bogdan.codeforceswatcher.network

import com.bogdan.codeforceswatcher.features.users.models.User
import com.bogdan.codeforceswatcher.network.models.Error
import com.bogdan.codeforceswatcher.network.models.UsersRequestResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun getUsers(handles: String, isRatingUpdatesNeeded: Boolean, onCompleted: (UsersRequestResult) -> Unit) {
    onCompleted(UsersRequestResult.Failure(Error.INTERNET))
    /*GlobalScope.launch {
        try {
            val response = RestClient.getUsers(handles)
            response.body()?.users?.let { users ->
                if (isRatingUpdatesNeeded) {
                    onCompleted(loadRatingUpdates(users))
                } else {
                    onCompleted(UsersRequestResult.Success(users))
                }
            } ?: onCompleted(UsersRequestResult.Failure(Error.RESPONSE))
        } catch (t: Throwable) {
            onCompleted(UsersRequestResult.Failure(Error.INTERNET))
        }
    }*/
}

suspend fun loadRatingUpdates(userList: List<User>): UsersRequestResult {
    var countFetchedUsers = 0
    for (user in userList) {
        delay(250) // Because Codeforces blocks frequent queries
        val response = try {
            RestClient.getRating(user.handle).execute()
        } catch (error: java.net.SocketTimeoutException) {
            null
        }
        response?.body()?.ratingChanges?.let { ratingChanges ->
            user.ratingChanges = ratingChanges
        } ?: break
        countFetchedUsers++
    }
    return if (countFetchedUsers < userList.size) {
        UsersRequestResult.Failure(Error.RESPONSE)
    } else {
        UsersRequestResult.Success(userList)
    }
}