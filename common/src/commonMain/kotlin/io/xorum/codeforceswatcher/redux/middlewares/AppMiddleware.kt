package io.xorum.codeforceswatcher.redux.middlewares

import io.xorum.codeforceswatcher.features.auth.getAuthStage
import io.xorum.codeforceswatcher.features.auth.redux.AuthRequests
import io.xorum.codeforceswatcher.features.auth.redux.AuthState
import io.xorum.codeforceswatcher.features.users.redux.requests.Source
import io.xorum.codeforceswatcher.features.users.redux.requests.UsersRequests
import io.xorum.codeforceswatcher.features.verification.VerificationRequests
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
            fetchUserData(action)
            fetchUsers(action)
            updateAuthStage(action)

            next(action)
        }
    }
}

private fun executeRequest(action: Request) = scope.launch { action.execute() }

private fun doActionsOnLogOut(action: Action) = scope.launch {
    if (action is AuthRequests.LogOut) {
        store.dispatch(UsersRequests.Destroy())
    }
}

private fun fetchUserData(action: Action) = scope.launch {
    val request = when (action) {
        is AuthRequests.SignIn.Success -> AuthRequests.FetchUserData(action.token, isSignUp = false)
        is AuthRequests.SignUp.Success -> AuthRequests.FetchUserData(action.token, isSignUp = true)
        is AuthRequests.FetchUserToken.Success -> AuthRequests.FetchUserData(action.token, isSignUp = false)
        else -> return@launch
    }
    store.dispatch(request)
}

private fun fetchUsers(action: Action) = scope.launch {
    if (action is AuthRequests.FetchUserToken.Failure) {
        store.dispatch(UsersRequests.FetchUsers(Source.BACKGROUND))
    }
}

private fun updateAuthStage(action: Action) = scope.launch {
    val authStage = when (action) {
        is AuthRequests.FetchUserData.Success -> action.userAccount.getAuthStage()
        is VerificationRequests.Verify.Success -> AuthState.Stage.VERIFIED
        is AuthRequests.LogOut -> AuthState.Stage.NOT_SIGNED_IN
        else -> return@launch
    }

    store.dispatch(AuthRequests.UpdateAuthStage(authStage))
}