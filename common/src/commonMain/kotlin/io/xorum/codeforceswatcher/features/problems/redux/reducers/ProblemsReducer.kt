package io.xorum.codeforceswatcher.features.problems.redux.reducers

import io.xorum.codeforceswatcher.features.problems.redux.actions.ProblemsActions
import io.xorum.codeforceswatcher.features.problems.redux.requests.ProblemsRequests
import io.xorum.codeforceswatcher.features.problems.redux.states.ProblemsState
import io.xorum.codeforceswatcher.redux.states.AppState
import tw.geothings.rekotlin.Action

fun problemsReducer(action: Action, state: AppState): ProblemsState {
    var newState = state.problems

    when (action) {
        is ProblemsRequests.FetchProblems -> {
            newState = newState.copy(status = ProblemsState.Status.PENDING)
        }
        is ProblemsRequests.FetchProblems.Success -> {
            newState = newState.copy(
                    problems = action.problems,
                    tags = action.tags,
                    status = ProblemsState.Status.IDLE
            )
        }
        is ProblemsRequests.FetchProblems.Failure -> {
            newState = newState.copy(status = ProblemsState.Status.IDLE)
        }
        is ProblemsActions.ChangeTypeProblems -> {
            newState = newState.copy(isFavourite = action.isFavourite)
        }
        is ProblemsRequests.ChangeStatusFavourite.Success -> {
            newState = newState.copy(problems = newState.problems.map {
                if (it.id == action.problem.id) action.problem else it
            })
        }
    }

    return newState
}
