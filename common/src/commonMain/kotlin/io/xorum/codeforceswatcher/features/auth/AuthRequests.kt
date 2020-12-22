package io.xorum.codeforceswatcher.features.auth

import io.xorum.codeforceswatcher.features.users.models.User
import io.xorum.codeforceswatcher.redux.Request
import io.xorum.codeforceswatcher.redux.store
import tw.geothings.rekotlin.Action

class AuthRequests {

    class SignIn : Request() {

        override suspend fun execute() {
            store.dispatch(Success(UserAccount(null, "token")))
        }

        data class Success(val userAccount: UserAccount) : Action
        object Failure : Action
    }

    class Verify : Request() {

        override suspend fun execute() {
            store.dispatch(Success(UserAccount(User(handle = "yevhenii", avatar = "https://userpic.codeforces.com/1592/avatar/7cef566902732053.jpg"), "token")))
        }

        data class Success(val userAccount: UserAccount) : Action
        object Failure : Action
    }
}
