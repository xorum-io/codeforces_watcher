package io.xorum.codeforceswatcher.features.verification.redux

import io.xorum.codeforceswatcher.redux.states.AppState
import tw.geothings.rekotlin.Action

fun verificationReducer(action: Action, state: AppState): VerificationState {
    var newState = state.verification

    when (action) {
        is VerificationRequests.FetchVerificationCode -> {
            newState = newState.copy(status = VerificationState.Status.PENDING)
        }
        is VerificationRequests.FetchVerificationCode.Success -> {
            newState = newState.copy(verificationCode = action.verificationCode)
            newState = newState.copy(status = VerificationState.Status.IDLE)
        }
        is VerificationRequests.VerifyCodeforces -> {
            newState = newState.copy(status = VerificationState.Status.PENDING)
        }
        is VerificationRequests.VerifyCodeforces.Success -> {
            newState = newState.copy(status = VerificationState.Status.DONE)
        }
        is VerificationRequests.VerifyCodeforces.Failure -> {
            newState = newState.copy(status = VerificationState.Status.IDLE)
        }
    }

    return newState
}
