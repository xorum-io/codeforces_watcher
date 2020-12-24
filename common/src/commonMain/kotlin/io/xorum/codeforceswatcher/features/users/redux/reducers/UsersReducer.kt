package io.xorum.codeforceswatcher.features.users.redux.reducers

import io.xorum.codeforceswatcher.features.auth.AuthRequests
import io.xorum.codeforceswatcher.features.users.redux.actions.UsersActions
import io.xorum.codeforceswatcher.features.users.redux.requests.UsersRequests
import io.xorum.codeforceswatcher.features.users.redux.states.UsersState
import io.xorum.codeforceswatcher.redux.states.AppState
import tw.geothings.rekotlin.Action

fun usersReducer(action: Action, state: AppState): UsersState {
    var newState = state.users

    when (action) {
        is UsersRequests.FetchUsers -> {
            newState = newState.copy(status = UsersState.Status.PENDING)
        }
        is UsersRequests.FetchUsers.Success -> {
            val mapUsers = action.users.associateBy { it.id }
            val newUsers = state.users.users.map { mapUsers[it.id] ?: it }
            newState = newState.copy(status = UsersState.Status.IDLE, users = newUsers)
        }
        is UsersRequests.FetchUsers.Failure -> {
            newState = newState.copy(status = UsersState.Status.IDLE)
        }
        is UsersRequests.DeleteUser -> {
            newState = newState.copy(users = state.users.users.minus(action.user))
        }
        is UsersActions.Sort -> {
            newState = newState.copy(sortType = action.sortType)
        }
        is UsersRequests.AddUser -> {
            newState = newState.copy(addUserStatus = UsersState.Status.PENDING)
        }
        is UsersRequests.AddUser.Failure -> {
            newState = newState.copy(addUserStatus = UsersState.Status.IDLE)
        }
        is UsersRequests.AddUser.Success -> {
            newState = newState.copy(users = newState.users.plus(action.user), addUserStatus = UsersState.Status.DONE)
        }
        is UsersActions.ClearAddUserState -> {
            newState = newState.copy(addUserStatus = UsersState.Status.IDLE)
        }
        is AuthRequests.SignIn.Success -> {
            newState = newState.copy(userAccount = action.userAccount)
        }
        is AuthRequests.Verify.Success -> {
            newState = newState.copy(userAccount = action.userAccount)
        }
        is AuthRequests.FetchUserAccount.Success -> {
            newState = newState.copy(userAccount = action.userAccount)
        }
    }

    return newState
}
