package com.bogdan.codeforceswatcher

import android.app.Application
import com.bogdan.codeforceswatcher.features.users.FirebaseController
import com.bogdan.codeforceswatcher.handlers.AndroidMessageHandler
import com.bogdan.codeforceswatcher.util.AnalyticsController
import com.bogdan.codeforceswatcher.util.Prefs
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.sqldelight.android.AndroidSqliteDriver
import io.xorum.codeforceswatcher.CWDatabase
import io.xorum.codeforceswatcher.features.FetchOnStartData
import io.xorum.codeforceswatcher.features.notifications.redux.NotificationsRequests
import io.xorum.codeforceswatcher.network.responses.backend.BACKEND_PROD_LINK
import io.xorum.codeforceswatcher.network.responses.backend.BACKEND_STAGING_LINK
import io.xorum.codeforceswatcher.network.responses.backend.backendLink
import io.xorum.codeforceswatcher.redux.*
import io.xorum.codeforceswatcher.redux.middlewares.toastHandlers
import io.xorum.codeforceswatcher.util.defineLang
import io.xorum.codeforceswatcher.util.settings
import java.util.*

class CwApp : Application() {

    override fun onCreate() {
        super.onCreate()

        app = this

        initDatabase()
        initSettings()
        initToastHandler()
        initAnalyticsController()
        initFirebaseController()

        persistenceController.onAppCreated()

        FirebaseAnalytics.getInstance(this)

        setBackendLink()
        initGetLang()
        initNotificationToken()

        fetchData()
    }

    private fun initDatabase() {
        sqlDriver = AndroidSqliteDriver(CWDatabase.Schema, app.applicationContext, "database")
    }

    private fun initSettings() {
        settings = Prefs.get()
    }

    private fun initToastHandler() {
        toastHandlers.add(AndroidMessageHandler())
    }

    private fun initAnalyticsController() {
        analyticsController = AnalyticsController()
    }

    private fun initFirebaseController() {
        firebaseController = FirebaseController()
    }

    private fun setBackendLink() = if (BuildConfig.DEBUG) {
        backendLink = BACKEND_STAGING_LINK
    } else {
        backendLink = BACKEND_PROD_LINK
    }

    private fun initGetLang() {
        getLang = {
            (Locale.getDefault().language).defineLang()
        }
    }

    private fun initNotificationToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) return@OnCompleteListener
            pushToken = task.result ?: return@OnCompleteListener
            store.state.users.userAccount?.let {
                store.dispatch(NotificationsRequests.AddPushToken)
            }
        })
    }

    private fun fetchData() = store.dispatch(FetchOnStartData)

    companion object {
        lateinit var app: CwApp
            private set
    }
}
