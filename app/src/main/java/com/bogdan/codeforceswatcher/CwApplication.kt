package com.bogdan.codeforceswatcher

import android.app.Application
import android.content.Intent
import com.bogdan.codeforceswatcher.features.actions.redux.requests.ActionsRequests
import com.bogdan.codeforceswatcher.features.contests.redux.requests.ContestsRequests
import com.bogdan.codeforceswatcher.features.users.redux.requests.Source
import com.bogdan.codeforceswatcher.features.users.redux.requests.UsersRequests
import com.bogdan.codeforceswatcher.receiver.StartAlarm
import com.bogdan.codeforceswatcher.redux.middlewares.appMiddleware
import com.bogdan.codeforceswatcher.redux.reducers.appReducer
import com.bogdan.codeforceswatcher.redux.middlewares.toastMiddleware
import com.bogdan.codeforceswatcher.redux.middlewares.notificationMiddleware
import com.bogdan.codeforceswatcher.room.RoomController
import com.bogdan.codeforceswatcher.util.PersistenceController
import com.bogdan.codeforceswatcher.util.Prefs
import com.google.firebase.analytics.FirebaseAnalytics
import org.rekotlin.Store

val store = Store(
    reducer = ::appReducer,
    state = RoomController.fetchAppState(),
    middleware = listOf(
        notificationMiddleware, toastMiddleware, appMiddleware
    )
)

class CwApp : Application() {

    override fun onCreate() {
        super.onCreate()

        app = this

        RoomController.onAppCreated()
        PersistenceController.onAppCreated()
        FirebaseAnalytics.getInstance(this)

        val prefs = Prefs.get()

        fetchData()

        if (prefs.readAlarm().isEmpty()) {
            startAlarm()
            prefs.writeAlarm("alarm")
        }

        prefs.addLaunchCount()
    }

    private fun fetchData() {
        store.dispatch(ActionsRequests.FetchActions(false))
        store.dispatch(ContestsRequests.FetchContests(false))
        store.dispatch(UsersRequests.FetchUsers(Source.BACKGROUND))
    }

    private fun startAlarm() {
        val intent = Intent(this, StartAlarm::class.java)
        sendBroadcast(intent)
    }

    companion object {

        lateinit var app: CwApp
            private set
    }
}
