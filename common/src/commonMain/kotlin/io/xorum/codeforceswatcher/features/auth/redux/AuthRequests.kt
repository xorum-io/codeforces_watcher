package io.xorum.codeforceswatcher.features.auth.redux

import io.xorum.codeforceswatcher.features.notifications.NotificationsRepository
import io.xorum.codeforceswatcher.redux.*
import tw.geothings.rekotlin.Action

class AuthRequests {

    class SignIn(
            private val email: String,
            private val password: String
    ) : Request() {

        override suspend fun execute() = firebaseController.signIn(email, password) { exception ->
            exception?.let {
                store.dispatch(Failure(exception.message.toMessage()))
            } ?: store.dispatch(Success)
        }

        object Success : Action
        data class Failure(override val message: Message) : ToastAction
    }

    class SignUp(
            private val email: String,
            private val password: String
    ) : Request() {

        override suspend fun execute() = firebaseController.signUp(email, password) { exception ->
            exception?.let {
                store.dispatch(Failure(exception.message.toMessage()))
            } ?: store.dispatch(Success)
        }

        object Success : Action
        data class Failure(override val message: Message) : ToastAction
    }

    data class UpdateAuthStage(val authStage: AuthState.Stage) : Action

    object LogOut : Request() {

        private val notificationsRepository = NotificationsRepository()

        override suspend fun execute() {
            pushToken?.let {
                notificationsRepository.deletePushToken(it)
            }
            firebaseController.logOut { exception ->
                exception?.let {
                    store.dispatch(Failure(it.message.toMessage()))
                } ?: store.dispatch(Success)
            }
        }

        object Success : Action
        data class Failure(override val message: Message) : ToastAction
    }

    class SendPasswordReset(private val email: String) : Request() {

        override suspend fun execute() = firebaseController.sendPasswordReset(email) { exception ->
            exception?.let {
                store.dispatch(Failure(it.message.toMessage()))
            } ?: store.dispatch(Success("Email with further instructions has been sent to you! Please check!".toMessage()))
            // TODO: translate string
        }

        data class Success(override val message: Message) : ToastAction
        data class Failure(override val message: Message) : ToastAction
    }
}
