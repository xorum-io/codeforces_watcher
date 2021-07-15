package io.xorum.codeforceswatcher.features.contests.redux

import io.xorum.codeforceswatcher.redux.states.AppState
import tw.geothings.rekotlin.Action

fun contestsReducer(action: Action, state: AppState): ContestsState {
    var newState = state.contests

    when (action) {
        is ContestsRequests.FetchContests -> {
            newState = newState.copy(status = ContestsState.Status.PENDING)
        }
        is ContestsRequests.FetchContests.Success -> {
            newState = newState.copy(
                    status = ContestsState.Status.IDLE,
                    contests = action.contests
            )
        }
        is ContestsRequests.FetchContests.Failure -> {
            newState = newState.copy(status = ContestsState.Status.IDLE)
        }

        is ContestsRequests.ChangeFilterCheckStatus -> {
            newState = newState.copy(
                    filters = if (action.isChecked) state.contests.filters.plus(action.platform)
                    else state.contests.filters.minus(action.platform)
            )
        }
    }

    return newState
}
