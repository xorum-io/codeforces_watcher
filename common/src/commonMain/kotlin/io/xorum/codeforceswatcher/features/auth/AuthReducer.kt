package io.xorum.codeforceswatcher.features.auth

import io.xorum.codeforceswatcher.features.verification.VerificationRequests
import io.xorum.codeforceswatcher.redux.states.AppState
import tw.geothings.rekotlin.Action

fun authReducer(action: Action, state: AppState): AuthState {
    var newState = state.auth

    when (action) {
        is AuthRequests.SignIn.Success -> {
            newState = newState.copy(
                    signInStatus = AuthState.Status.DONE,
                    authStage = action.authStage
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
                    signInStatus = AuthState.Status.DONE,
                    authStage = AuthState.Stage.SIGNED_IN
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
        is VerificationRequests.Verify.Success -> {
            newState = newState.copy(
                    authStage = AuthState.Stage.VERIFIED
            )
        }
        is AuthRequests.LogOut -> {
            newState = newState.copy(authStage = AuthState.Stage.NOT_SIGNED_IN)
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
