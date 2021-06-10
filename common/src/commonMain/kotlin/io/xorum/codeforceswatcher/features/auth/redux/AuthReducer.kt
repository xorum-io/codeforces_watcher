package io.xorum.codeforceswatcher.features.auth.redux

import io.xorum.codeforceswatcher.redux.states.AppState
import tw.geothings.rekotlin.Action

fun authReducer(action: Action, state: AppState): AuthState {
    var newState = state.auth

    when (action) {
        is AuthRequests.SignIn -> {
            newState = newState.copy(
                    status = AuthState.Status.PENDING
            )
        }
        is AuthRequests.SignIn.Success -> {
            newState = newState.copy(
                    status = AuthState.Status.DONE
            )
        }
        is AuthRequests.SignIn.Failure -> {
            newState = newState.copy(
                    status = AuthState.Status.IDLE
            )
        }
        is AuthRequests.SignUp -> {
            newState = newState.copy(
                    status = AuthState.Status.PENDING
            )
        }
        is AuthRequests.SignUp.Success -> {
            newState = newState.copy(
                    status = AuthState.Status.DONE
            )
        }
        is AuthRequests.SignUp.Failure -> {
            newState = newState.copy(
                    status = AuthState.Status.IDLE
            )
        }
        is AuthRequests.UpdateAuthStage -> {
            newState = newState.copy(authStage = action.authStage)
        }
        is AuthRequests.LogOut.Success -> {
            newState = AuthState()
        }
    }

    return newState
}
