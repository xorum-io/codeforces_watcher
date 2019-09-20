package com.bogdan.codeforceswatcher.redux

import com.bogdan.codeforceswatcher.feature.contests.redux.ContestsState
import com.bogdan.codeforceswatcher.feature.users.redux.UsersState
import org.rekotlin.StateType

data class AppState(
    val contests: ContestsState = ContestsState(),
    val users: UsersState = UsersState()
) : StateType