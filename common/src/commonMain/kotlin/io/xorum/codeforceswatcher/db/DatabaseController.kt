package io.xorum.codeforceswatcher.db

import io.xorum.codeforceswatcher.features.auth.models.getAuthStage
import io.xorum.codeforceswatcher.features.auth.redux.AuthState
import io.xorum.codeforceswatcher.features.contests.models.Contest
import io.xorum.codeforceswatcher.features.contests.redux.ContestsState
import io.xorum.codeforceswatcher.features.problems.redux.states.ProblemsState
import io.xorum.codeforceswatcher.features.users.redux.UsersState
import io.xorum.codeforceswatcher.redux.states.AppState
import io.xorum.codeforceswatcher.util.settings

class DatabaseController {

    fun fetchAppState() = AppState(
            contests = ContestsState(
                    filters = settings.readContestsFilters().map { Contest.Platform.valueOf(it) }.toMutableSet()
            ),
            users = UsersState(
                    users = DatabaseQueries.Users.getAll(),
                    sortType = UsersState.SortType.getSortType(settings.readSpinnerSortPosition()),
                    userAccount = settings.readUserAccount(),
            ),
            problems = ProblemsState(
                    problems = DatabaseQueries.Problems.getAll(),
                    isFavourite = (settings.readProblemsIsFavourite())
            ),
            auth = AuthState(authStage = settings.readUserAccount().getAuthStage())
    )
}
