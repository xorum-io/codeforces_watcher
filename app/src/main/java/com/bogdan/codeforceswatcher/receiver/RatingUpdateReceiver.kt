package com.bogdan.codeforceswatcher.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.xorum.codeforceswatcher.features.users.redux.FetchUserDataSource
import io.xorum.codeforceswatcher.features.users.redux.Source
import io.xorum.codeforceswatcher.features.users.redux.UsersRequests
import io.xorum.codeforceswatcher.redux.store

class RatingUpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        store.dispatch(UsersRequests.FetchUserData(FetchUserDataSource.PULL_TO_REFRESH, Source.BROADCAST))
    }
}
