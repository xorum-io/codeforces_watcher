package io.xorum.codeforceswatcher.features.users.redux.reducers

import io.xorum.codeforceswatcher.features.auth.redux.AuthRequests
import io.xorum.codeforceswatcher.features.users.redux.actions.UsersActions
import io.xorum.codeforceswatcher.features.users.redux.requests.UsersRequests
import io.xorum.codeforceswatcher.features.users.redux.states.UsersState
import io.xorum.codeforceswatcher.features.verification.redux.VerificationRequests
import io.xorum.codeforceswatcher.redux.states.AppState
import tw.geothings.rekotlin.Action

fun usersReducer(action: Action, state: AppState): UsersState {
    var newState = state.users

    when (action) {
        is UsersRequests.FetchUserData -> {
            newState = newState.copy(status = UsersState.Status.PENDING)
        }
        is UsersRequests.FetchUserData.Success -> {
            newState = newState.copy(
                    status = UsersState.Status.IDLE,
                    users = action.users,
                    userAccount = action.userAccount
            )
        }
        is UsersRequests.FetchUserData.Failure -> {
            newState = newState.copy(status = UsersState.Status.IDLE)
        }
        is UsersRequests.FetchUser -> {
            newState = newState.copy(
                    status = UsersState.Status.PENDING,
                    currentUser = (state.users.users + state.users.userAccount?.codeforcesUser).find { it?.handle == action.handle }
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
            newState = newState.copy(status = UsersState.Status.PENDING)
        }
        is UsersRequests.DeleteUser.Success -> {
            newState = newState.copy(
                    users = state.users.users.minus(action.user),
                    status = UsersState.Status.DONE
            )
        }
        is UsersRequests.DeleteUser.Failure -> {
            newState = newState.copy(status = UsersState.Status.IDLE)
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
        is AuthRequests.FetchFirebaseUserToken -> {
            newState = newState.copy(
                    status = UsersState.Status.PENDING
            )
        }
        is AuthRequests.FetchFirebaseUserToken.Success -> {
            newState = newState.copy(
                    status = UsersState.Status.IDLE
            )
        }
        is AuthRequests.FetchFirebaseUserToken.Failure -> {
            newState = newState.copy(
                    status = UsersState.Status.IDLE
            )
        }
        is VerificationRequests.VerifyCodeforces.Success -> {
            newState = newState.copy(userAccount = action.userAccount)
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
