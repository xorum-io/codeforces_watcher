package io.xorum.codeforceswatcher.redux.reducers

import io.xorum.codeforceswatcher.features.auth.authReducer
import io.xorum.codeforceswatcher.features.news.redux.reducers.newsReducer
import io.xorum.codeforceswatcher.features.users.redux.reducers.usersReducer
import io.xorum.codeforceswatcher.features.problems.redux.reducers.problemsReducer
import io.xorum.codeforceswatcher.features.contests.redux.reducers.contestsReducer
import io.xorum.codeforceswatcher.features.verification.verificationReducer
import io.xorum.codeforceswatcher.redux.states.AppState
import tw.geothings.rekotlin.Action

fun appReducer(action: Action, state: AppState?): AppState {
    requireNotNull(state)
    return AppState(
            contests = contestsReducer(action, state),
            users = usersReducer(action, state),
            news = newsReducer(action, state),
            problems = problemsReducer(action, state),
            auth = authReducer(action, state),
            verification = verificationReducer(action, state)
    )
}
