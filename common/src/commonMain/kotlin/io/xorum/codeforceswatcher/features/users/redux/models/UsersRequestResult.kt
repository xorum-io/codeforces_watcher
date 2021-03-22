package io.xorum.codeforceswatcher.features.users.redux.models

import io.xorum.codeforceswatcher.features.users.models.User
import io.xorum.codeforceswatcher.redux.Message

internal sealed class Error(val message: Message) {
    class Internet(message: Message = Message.NoConnection) : Error(message)
    class Response(message: Message = Message.FailedToFetchUser) : Error(message)
}

internal sealed class UsersRequestResult {
    data class Success(val users: List<User>) : UsersRequestResult()
    data class Failure(val error: Error) : UsersRequestResult()
}
