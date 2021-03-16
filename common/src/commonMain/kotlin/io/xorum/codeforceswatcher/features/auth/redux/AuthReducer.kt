package io.xorum.codeforceswatcher.features.auth.redux

import io.xorum.codeforceswatcher.redux.states.AppState
import tw.geothings.rekotlin.Action

fun authReducer(action: Action, state: AppState): AuthState {
    var newState = state.auth

    when (action) {
        is AuthRequests.SignIn -> {
            newState = newState.copy(
                    signInStatus = AuthState.Status.PENDING
            )
        }
        is AuthRequests.SignIn.Success -> {
            newState = newState.copy(
                    signInStatus = AuthState.Status.DONE
            )
        }
        is AuthRequests.SignIn.Failure -> {
            newState = newState.copy(
                    signInStatus = AuthState.Status.IDLE
            )
        }
        is AuthRequests.SignUp -> {
            newState = newState.copy(
                    signUpStatus = AuthState.Status.PENDING
            )
        }
        is AuthRequests.SignUp.Success -> {
            newState = newState.copy(
                    signUpStatus = AuthState.Status.DONE,
                    signInStatus = AuthState.Status.DONE
            )
        }
        is AuthRequests.SignUp.Failure -> {
            newState = newState.copy(
                    signUpStatus = AuthState.Status.IDLE
            )
        }
        is AuthRequests.FetchUserToken.Success -> {
            newState = newState.copy(token = action.token)
        }
        is AuthRequests.UpdateAuthStage -> {
            newState = newState.copy(authStage = action.authStage)
        }
        is AuthRequests.DestroyStatus -> {
            newState = newState.copy(
                    signInStatus = AuthState.Status.IDLE,
                    signUpStatus = AuthState.Status.IDLE
            )
        }
    }

    return newState
}
