package io.xorum.codeforceswatcher.db

import io.xorum.codeforceswatcher.features.auth.AuthState
import io.xorum.codeforceswatcher.features.auth.UserAccount
import io.xorum.codeforceswatcher.features.contests.models.Platform
import io.xorum.codeforceswatcher.features.contests.redux.states.ContestsState
import io.xorum.codeforceswatcher.features.problems.redux.states.ProblemsState
import io.xorum.codeforceswatcher.features.users.redux.states.UsersState
import io.xorum.codeforceswatcher.redux.states.AppState
import io.xorum.codeforceswatcher.redux.store
import io.xorum.codeforceswatcher.util.settings
import tw.geothings.rekotlin.StoreSubscriber

class DatabaseController : StoreSubscriber<AppState> {

    fun onAppCreated() {
        store.subscribe(this) {
            it.skipRepeats { oldState, newState ->
                oldState.contests == newState.contests
            }
        }
    }

    fun fetchAppState() = AppState(
            contests = ContestsState(
                    contests = DatabaseQueries.Contests.getAll(),
                    filters = settings.readContestsFilters().map { Platform.valueOf(it) }.toMutableSet()
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

    private fun UserAccount?.getAuthStage() = when {
        this?.codeforcesUser != null -> AuthState.Stage.VERIFIED
        this != null -> AuthState.Stage.SIGNED_IN
        else -> AuthState.Stage.NOT_SIGNED_IN
    }

    override fun onNewState(state: AppState) {
        if (DatabaseQueries.Contests.getAll() != state.contests.contests.sortedBy { it.id }) {
            DatabaseQueries.Contests.deleteAll()
            DatabaseQueries.Contests.insert(state.contests.contests)
        }
    }
}
