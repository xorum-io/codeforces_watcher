package com.bogdan.codeforceswatcher.features.users.redux.reducers

import com.bogdan.codeforceswatcher.features.add_user.redux.requests.AddUserRequests
import com.bogdan.codeforceswatcher.features.users.redux.actions.UsersActions
import com.bogdan.codeforceswatcher.features.users.redux.requests.UsersRequests
import com.bogdan.codeforceswatcher.features.users.redux.states.UsersState
import com.bogdan.codeforceswatcher.redux.states.AppState
import org.rekotlin.Action

fun usersReducer(action: Action, state: AppState): UsersState {
    var newState = state.users
    println("usersReducer is working")
    when (action) {
        is UsersRequests.FetchUsers -> {
            println("reducer continue")
            newState = newState.copy(
                status = UsersState.Status.PENDING
            )
        }
        is UsersRequests.FetchUsers.Success -> {
            println("reducer success")
            newState = newState.copy(
                status = UsersState.Status.IDLE,
                users = action.users
            )
        }
        is UsersRequests.FetchUsers.Failure -> {
            println("reducer failure stop")
            newState = newState.copy(
                status = UsersState.Status.IDLE
            )
        }

        is AddUserRequests.AddUser.Success -> {
            newState = newState.copy(
                users = state.users.users.plus(action.user)
            )
        }

        is UsersActions.DeleteUser -> {
            newState = newState.copy(
                users = state.users.users.minus(action.user)
            )
        }

        is UsersActions.Sort -> {
            newState = newState.copy(
                sortType = action.sortType
            )
        }
    }

    return newState
}
