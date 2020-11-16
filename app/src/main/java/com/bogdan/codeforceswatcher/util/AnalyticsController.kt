package com.bogdan.codeforceswatcher.util

import android.os.Bundle
import com.bogdan.codeforceswatcher.CwApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.xorum.codeforceswatcher.util.IAnalyticsController

class AnalyticsController : IAnalyticsController {

    private val instance = FirebaseAnalytics.getInstance(CwApp.app)

    override fun logError(message: String) {
        FirebaseCrashlytics.getInstance().recordException(Throwable(message))
    }

    override fun logEvent(eventName: String, params: Map<String, String>) {
        instance.logEvent(eventName, params.toBundle())
    }

    private fun Map<String, String>.toBundle() = Bundle().apply {
        for (entry in entries)
            putString(entry.key, entry.value)
    }
}
