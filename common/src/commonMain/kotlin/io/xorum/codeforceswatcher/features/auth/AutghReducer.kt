package io.xorum.codeforceswatcher.features.auth

import io.xorum.codeforceswatcher.redux.states.AppState
import tw.geothings.rekotlin.Action

fun authReducer(action: Action, state: AppState): AuthState {
    var newState = state.auth

    when (action) {

        is AuthRequests.SignIn.Success -> {
            newState = newState.copy(
                    signInStatus = AuthState.Status.DONE
            )
        }
        is AuthRequests.SignIn -> {
            newState = newState.copy(
                    signInStatus = AuthState.Status.PENDING
            )
        }
        is AuthRequests.SignIn.Failure -> {
            newState = newState.copy(
                    signInStatus = AuthState.Status.IDLE
            )
        }

        is AuthRequests.SignUp.Success -> {
            newState = newState.copy(
                    signUpStatus = AuthState.Status.DONE,
                    signInStatus = AuthState.Status.DONE
            )
        }
        is AuthRequests.SignUp -> {
            newState = newState.copy(
                    signUpStatus = AuthState.Status.PENDING
            )
        }
        is AuthRequests.SignUp.Failure -> {
            newState = newState.copy(
                    signUpStatus = AuthState.Status.IDLE
            )
        }
    }

    return newState
}
