package io.xorum.codeforceswatcher.features.users.redux.reducers

import io.xorum.codeforceswatcher.features.auth.redux.AuthRequests
import io.xorum.codeforceswatcher.features.users.redux.actions.UsersActions
import io.xorum.codeforceswatcher.features.users.redux.requests.UsersRequests
import io.xorum.codeforceswatcher.features.users.redux.states.UsersState
import io.xorum.codeforceswatcher.features.verification.VerificationRequests
import io.xorum.codeforceswatcher.redux.states.AppState
import tw.geothings.rekotlin.Action

fun usersReducer(action: Action, state: AppState): UsersState {
    var newState = state.users

    when (action) {
        is UsersRequests.FetchUsers -> {
            newState = newState.copy(status = UsersState.Status.PENDING)
        }
        is UsersRequests.FetchUsers.Success -> {
            // Looks like it's needed for saving same order as were before fetching
            val mapUsers = action.users.associateBy { it.handle }
            val newUsers = state.users.users.map { mapUsers[it.handle] ?: it }
            newState = newState.copy(
                    status = UsersState.Status.IDLE,
                    users = newUsers,
                    userAccount = state.users.userAccount?.copy(codeforcesUser = action.userAccountCfUser)
            )
        }
        is UsersRequests.FetchUsers.Failure -> {
            newState = newState.copy(status = UsersState.Status.IDLE)
        }
        is UsersRequests.FetchUser -> {
            newState = newState.copy(
                    status = UsersState.Status.PENDING,
                    currentUser = state.users.users.find { it.handle == action.handle }
            )
        }
        is UsersRequests.FetchUser.Success -> {
            newState = newState.copy(
                    status = UsersState.Status.IDLE,
                    currentUser = action.user,
                    users = state.users.users.map { if (it.handle == action.user.handle) action.user else it }
            )
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
            newState = newState.copy(
                    userAccount = action.userAccount
            )
        }
        is AuthRequests.SignUp.Success -> {
            newState = newState.copy(
                    userAccount = action.userAccount
            )
        }
        is VerificationRequests.Verify.Success -> {
            newState = newState.copy(userAccount = action.userAccount)
        }
        is UsersRequests.ClearCurrentUser -> {
            newState = newState.copy(currentUser = null)
        }
        is UsersRequests.Destroy -> {
            newState = newState.copy(
                    userAccount = null,
                    users = listOf()
            )
        }
    }

    return newState
}
