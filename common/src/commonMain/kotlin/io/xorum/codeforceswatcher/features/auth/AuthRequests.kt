package io.xorum.codeforceswatcher.features.auth

import io.xorum.codeforceswatcher.network.responses.backend.Response
import io.xorum.codeforceswatcher.redux.*
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
                    val userAccount = response.result

                    val authStage = if (userAccount.codeforcesUser != null) {
                        AuthState.Stage.VERIFIED
                    } else {
                        AuthState.Stage.SIGNED_IN
                    }

                    store.dispatch(Success(userAccount, authStage))
                    settings.writeUserAccount(userAccount)
                }
                is Response.Failure -> {
                    store.dispatch(Failure(response.error.toMessage()))
                }
            }
        }

        data class Success(
                val userAccount: UserAccount,
                val authStage: AuthState.Stage
        ) : Action

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

    object DestroyStatus : Action
    object LogOut : Action
}
