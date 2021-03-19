package com.bogdan.codeforceswatcher.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.xorum.codeforceswatcher.features.users.redux.requests.Source
import io.xorum.codeforceswatcher.features.users.redux.requests.UsersRequests
import io.xorum.codeforceswatcher.redux.store

class RatingUpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val token = store.state.auth.token
        val users = if (token.isNullOrBlank()) store.state.users.users else emptyList()
        store.dispatch(UsersRequests.FetchUserData(token, users, Source.BROADCAST))
    }
}
