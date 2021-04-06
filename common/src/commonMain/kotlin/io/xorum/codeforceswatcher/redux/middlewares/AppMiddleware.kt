package io.xorum.codeforceswatcher.redux.middlewares

import io.xorum.codeforceswatcher.features.FetchOnStartData
import io.xorum.codeforceswatcher.features.auth.models.getAuthStage
import io.xorum.codeforceswatcher.features.auth.redux.AuthRequests
import io.xorum.codeforceswatcher.features.auth.redux.AuthState
import io.xorum.codeforceswatcher.features.contests.redux.requests.ContestsRequests
import io.xorum.codeforceswatcher.features.news.redux.NewsRequests
import io.xorum.codeforceswatcher.features.problems.redux.requests.ProblemsRequests
import io.xorum.codeforceswatcher.features.users.redux.FetchUserDataType
import io.xorum.codeforceswatcher.features.users.redux.Source
import io.xorum.codeforceswatcher.features.users.redux.UsersRequests
import io.xorum.codeforceswatcher.features.verification.redux.VerificationRequests
import io.xorum.codeforceswatcher.redux.Request
import io.xorum.codeforceswatcher.redux.analyticsController
import io.xorum.codeforceswatcher.redux.getLang
import io.xorum.codeforceswatcher.redux.store
import io.xorum.codeforceswatcher.util.AnalyticsEvents
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
            fetchUsersData(action)
            updateAuthStage(action)
            sendAnalytics(action)
            fetchOnStartData(action)

            next(action)
        }
    }
}

private fun executeRequest(action: Request) = scope.launch { action.execute() }

private fun doActionsOnLogOut(action: Action) = scope.launch {
    if (action is AuthRequests.LogOut.Success) {
        store.dispatch(UsersRequests.Destroy())
    }
}

private fun fetchUsersData(action: Action) = scope.launch {
    val request = when (action) {
        is AuthRequests.SignIn.Success -> UsersRequests.FetchUserData(FetchUserDataType.REFRESH, Source.BACKGROUND)
        is AuthRequests.SignUp.Success -> UsersRequests.FetchUserData(FetchUserDataType.PERSIST, Source.BACKGROUND)
        else -> return@launch
    }
    store.dispatch(request)
}

private fun updateAuthStage(action: Action) = scope.launch {
    val authStage = when (action) {
        is AuthRequests.SignIn.Success -> AuthState.Stage.SIGNED_IN
        is AuthRequests.SignUp.Success -> AuthState.Stage.SIGNED_IN
        is UsersRequests.FetchUserData.Success -> action.userAccount.getAuthStage()
        is VerificationRequests.VerifyCodeforces.Success -> AuthState.Stage.VERIFIED
        is AuthRequests.LogOut.Success -> AuthState.Stage.NOT_SIGNED_IN
        else -> return@launch
    }

    store.dispatch(AuthRequests.UpdateAuthStage(authStage))
}

private fun sendAnalytics(action: Action) = scope.launch {
    val (event, params: Map<String, String>?) = when (action) {
        is AuthRequests.SignIn.Success -> Pair(AnalyticsEvents.SIGN_IN_DONE, null)
        is AuthRequests.SignUp.Success -> Pair(AnalyticsEvents.SIGN_UP_DONE, null)
        is AuthRequests.LogOut.Success -> Pair(AnalyticsEvents.LOG_OUT, null)
        is AuthRequests.SendPasswordReset.Success -> Pair(AnalyticsEvents.RESTORE_PASSWORD, null)

        is VerificationRequests.VerifyCodeforces.Success -> Pair(AnalyticsEvents.VERIFY_DONE, mapOf("platform" to "codeforces"))

        is UsersRequests.FetchUserData.Success -> Pair(AnalyticsEvents.FETCH_USERS_SUCCESS, null)
        is UsersRequests.FetchUserData.Failure -> Pair(AnalyticsEvents.FETCH_USERS_FAILURE, null)
        is UsersRequests.AddUser.Success -> Pair(AnalyticsEvents.USER_ADDED, null)

        is NewsRequests.FetchNews -> Pair(AnalyticsEvents.NEWS_FETCH, null)
        is NewsRequests.FetchNews.Success -> Pair(AnalyticsEvents.NEWS_FETCH_SUCCESS, null)
        is NewsRequests.FetchNews.Failure -> Pair(AnalyticsEvents.NEWS_FETCH_FAILURE, null)
        is NewsRequests.RemovePinnedPost -> Pair(AnalyticsEvents.PINNED_POST_CLOSED, null)

        else -> return@launch
    }

    analyticsController.logEvent(event, params ?: mapOf())
}

private fun fetchOnStartData(action: Action) = scope.launch {
    if (action is FetchOnStartData) {
        store.dispatch(UsersRequests.FetchUserData(FetchUserDataType.REFRESH, Source.BACKGROUND))
        store.dispatch(NewsRequests.FetchNews(isInitiatedByUser = false))
        store.dispatch(ContestsRequests.FetchContests(isInitiatedByUser = false, getLang()))
        store.dispatch(ProblemsRequests.FetchProblems(isInitiatedByUser = false))
    }
}