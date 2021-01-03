package io.xorum.codeforceswatcher.features.auth

import io.xorum.codeforceswatcher.features.users.models.User
import io.xorum.codeforceswatcher.network.responses.Response
import io.xorum.codeforceswatcher.redux.*
import io.xorum.codeforceswatcher.redux.backendRepository
import io.xorum.codeforceswatcher.util.settings
import tw.geothings.rekotlin.Action

class AuthRequests {

    class SignIn(
            private val email: String,
            private val password: String
    ) : Request() {

        override suspend fun execute() {
            when (val response = backendRepository.signIn(email, password)) {
                is Response.Success -> {
                    store.dispatch(Success(response.result))
                    settings.writeUserAccount(response.result)
                }
                is Response.Failure -> {
                    store.dispatch(Failure(response.error.toMessage()))
                }
            }
        }

        data class Success(val userAccount: UserAccount) : Action
        data class Failure(override val message: Message) : ToastAction
    }

    class SignUp(
            private val email: String,
            private val password: String
    ) : Request() {

        override suspend fun execute() {
            when (val response = backendRepository.signUp(email, password)) {
                is Response.Success -> {
                    store.dispatch(Success(response.result))
                    settings.writeUserAccount(response.result)
                }
                is Response.Failure -> {
                    store.dispatch(Failure(response.error.toMessage()))
                }
            }
        }

        data class Success(val userAccount: UserAccount) : Action
        data class Failure(override val message: Message) : ToastAction
    }

    class Verify : Request() {

        override suspend fun execute() {
            store.dispatch(Success(UserAccount(User(handle = "yevhenii", avatar = "https://userpic.codeforces.com/1592/avatar/7cef566902732053.jpg"), "token", "bohdan")))
        }

        data class Success(val userAccount: UserAccount) : Action
        object Failure : Action
    }

    class FetchUserAccount : Request() {

        override suspend fun execute() {
            store.dispatch(Success(settings.readUserAccount()))
        }

        data class Success(val userAccount: UserAccount?) : Action
    }
}