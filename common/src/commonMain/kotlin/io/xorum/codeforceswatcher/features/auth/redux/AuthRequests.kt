package io.xorum.codeforceswatcher.features.auth.redux

import io.xorum.codeforceswatcher.redux.*
import tw.geothings.rekotlin.Action

class AuthRequests {

    class FetchFirebaseUserToken : Request() {

        override suspend fun execute() = firebaseController.fetchToken { token, e ->
            token?.let {
                store.dispatch(Success(token))
            } ?: store.dispatch(Failure(e?.message.toMessage()))
        }

        data class Success(val token: String) : Action
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

        override suspend fun execute() = firebaseController.logOut { e ->
            e?.let {
                store.dispatch(Failure(e.message.toMessage()))
            } ?: store.dispatch(Success)
        }

        object Success : Action
        data class Failure(override val message: Message) : ToastAction
    }

    class SendPasswordReset(private val email: String) : Request() {

        override suspend fun execute() = firebaseController.sendPasswordReset(email) { e ->
            e?.let {
                store.dispatch(Failure(e.message.toMessage()))
            } ?: store.dispatch(Success("Email with further instructions was sent to you! Please check!".toMessage()))
        }

        data class Success(override val message: Message) : ToastAction
        data class Failure(override val message: Message) : ToastAction
    }
}
