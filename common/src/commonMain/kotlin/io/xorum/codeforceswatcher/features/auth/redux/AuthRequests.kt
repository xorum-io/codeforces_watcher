package io.xorum.codeforceswatcher.features.auth.redux

import io.xorum.codeforceswatcher.features.auth.UserAccount
import io.xorum.codeforceswatcher.features.users.models.User
import io.xorum.codeforceswatcher.network.UserAccountRepository
import io.xorum.codeforceswatcher.network.responses.backend.Response
import io.xorum.codeforceswatcher.redux.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tw.geothings.rekotlin.Action

class AuthRequests {

    class FetchUserToken : Request() {

        override suspend fun execute() = firebaseController.fetchToken { token, e ->
            token?.let {
                store.dispatch(Success(token))
            } ?: store.dispatch(Failure(e?.message.toMessage()))
        }

        data class Success(val token: String) : Action
        data class Failure(override val message: Message) : ToastAction
    }

    class FetchUserData(token: String, private val isSignUp: Boolean) : Request() {

        private val userAccountRepository = UserAccountRepository(token)

        override suspend fun execute() {
            val users = if (isSignUp) store.state.users.users else emptyList()
            when (val response = userAccountRepository.fetchUserData(users)) {
                is Response.Success -> {
                    // TODO: put to db
                    store.dispatch(Success(response.result.users, response.result.userAccount))
                }
                is Response.Failure -> store.dispatch(Failure(response.error.toMessage()))
            }
        }

        data class Success(
                val users: List<User>,
                val userAccount: UserAccount
        ) : Action

        data class Failure(override val message: Message) : ToastAction
    }

    class SignIn(
            private val email: String,
            private val password: String
    ) : Request() {

        override suspend fun execute() = firebaseController.signIn(email, password) { token, e ->
            token?.let {
                store.dispatch(Success(token))
            } ?: store.dispatch(Failure(e?.message.toMessage()))
        }

        data class Success(val token: String) : Action
        data class Failure(override val message: Message) : ToastAction
    }

    class SignUp(
            private val email: String,
            private val password: String
    ) : Request() {

        override suspend fun execute() = firebaseController.signUp(email, password) { token, e ->
            token?.let {
                store.dispatch(Success(token))
            } ?: store.dispatch(Failure(e?.message.toMessage()))
        }

        data class Success(val token: String) : Action
        data class Failure(override val message: Message) : ToastAction
    }

    data class UpdateAuthStage(val authStage: AuthState.Stage) : Action
    object DestroyStatus : Action

    object LogOut : Request() {

        override suspend fun execute() = firebaseController.logOut()
    }
}
