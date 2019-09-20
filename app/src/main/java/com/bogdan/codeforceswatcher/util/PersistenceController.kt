package com.bogdan.codeforceswatcher.util

import com.bogdan.codeforceswatcher.feature.users.redux.UsersState.SortType.Companion.getPositionFromSortType
import com.bogdan.codeforceswatcher.redux.AppState
import com.bogdan.codeforceswatcher.store
import org.rekotlin.StoreSubscriber

object PersistenceController : StoreSubscriber<AppState> {

    fun onAppCreated() {
        store.subscribe(this) {
            it.skipRepeats { oldState, newState ->
                oldState.users.sortType == newState.users.sortType
            }
        }
    }

    override fun newState(state: AppState) {
        Prefs.get().writeSpinnerSortPosition(getPositionFromSortType(state.users.sortType))
    }

}