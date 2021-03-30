package io.xorum.codeforceswatcher.redux.middlewares

import io.xorum.codeforceswatcher.features.auth.models.getAuthStage
import io.xorum.codeforceswatcher.features.auth.redux.AuthRequests
import io.xorum.codeforceswatcher.features.auth.redux.AuthState
import io.xorum.codeforceswatcher.features.users.redux.requests.Source
import io.xorum.codeforceswatcher.features.users.redux.requests.UsersRequests
import io.xorum.codeforceswatcher.features.verification.redux.VerificationRequests
import io.xorum.codeforceswatcher.redux.Request
import io.xorum.codeforceswatcher.redux.store
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import tw.geothings.rekotlin.Action
import tw.geothings.rekotlin.Middleware
import tw.geothings.rekotlin.StateType

private val scope = MainScope()

val appMiddleware: Middleware<StateType> = { _, _ ->
    { next ->
        { action ->
            if (action is Request) executeRequest(action)

            doActionsOnLogOut(action)
            fetchUsersData(action)
            updateAuthStage(action)

            next(action)
        }
    }
}

private fun executeRequest(action: Request) = scope.launch { action.execute() }

private fun doActionsOnLogOut(action: Action) = scope.launch {
    if (action is AuthRequests.LogOut.Success) {
        store.dispatch(UsersRequests.Destroy())
    }
}

private fun fetchUsersData(action: Action) = scope.launch {
    val request = when (action) {
        is AuthRequests.SignIn.Success -> UsersRequests.FetchUserData(action.token, emptyList(), Source.BACKGROUND)
        is AuthRequests.SignUp.Success -> UsersRequests.FetchUserData(action.token, store.state.users.users, Source.BACKGROUND)
        is AuthRequests.FetchFirebaseUserToken.Success -> {
            val users = store.state.users.users.takeIf { action.token != null }.orEmpty()
            UsersRequests.FetchUserData(action.token, users, Source.BACKGROUND)
        }
        else -> return@launch
    }
    store.dispatch(request)
}

private fun updateAuthStage(action: Action) = scope.launch {
    val authStage = when (action) {
        is AuthRequests.SignIn.Success -> AuthState.Stage.SIGNED_IN
        is AuthRequests.SignUp.Success -> AuthState.Stage.SIGNED_IN
        is UsersRequests.FetchUserData.Success -> action.userAccount.getAuthStage()
        is VerificationRequests.Verify.Success -> AuthState.Stage.VERIFIED
        is AuthRequests.LogOut.Success -> AuthState.Stage.NOT_SIGNED_IN
        else -> return@launch
    }

    store.dispatch(AuthRequests.UpdateAuthStage(authStage))
}