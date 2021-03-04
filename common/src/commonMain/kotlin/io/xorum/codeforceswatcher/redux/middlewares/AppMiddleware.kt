package io.xorum.codeforceswatcher.redux.middlewares

import io.xorum.codeforceswatcher.features.auth.AuthRequests
import io.xorum.codeforceswatcher.features.users.redux.requests.UsersRequests
import io.xorum.codeforceswatcher.redux.Request
import io.xorum.codeforceswatcher.redux.store
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import tw.geothings.rekotlin.Action
import tw.geothings.rekotlin.Middleware
import tw.geothings.rekotlin.StateType

private val scope = MainScope()

val appMiddleware: Middleware<StateType> = { _, _ ->
    { next ->
        { action ->
            if (action is Request) executeRequest(action)

            doActionsOnLogOut(action)

            next(action)
        }
    }
}

private fun executeRequest(action: Request) = scope.launch { action.execute() }

private fun doActionsOnLogOut(action: Action) = scope.launch {
    if (action is AuthRequests.LogOut) {
        store.dispatch(UsersRequests.Destroy())
    }
}
