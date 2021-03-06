package com.bogdan.codeforceswatcher.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.xorum.codeforceswatcher.features.users.redux.requests.Source
import io.xorum.codeforceswatcher.features.users.redux.requests.UsersRequests
import io.xorum.codeforceswatcher.redux.store
import java.util.*

class RatingUpdateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        store.dispatch(UsersRequests.FetchUsers(Source.BROADCAST))
    }
}
