package com.bogdan.codeforceswatcher.util

import android.os.Bundle
import com.bogdan.codeforceswatcher.CwApp
import com.google.firebase.analytics.FirebaseAnalytics
import io.xorum.codeforceswatcher.features.contests.models.Platform
import io.xorum.codeforceswatcher.util.IAnalyticsController
import io.xorum.codeforceswatcher.util.RefreshScreen

class AnalyticsController : IAnalyticsController {

    private var isEnabled: Boolean = true

    private val instance = FirebaseAnalytics.getInstance(CwApp.app)

    override fun logAddContestToCalendarEvent(contestName: String, platform: Platform) {
        if (isEnabled) {
            val params = Bundle()
            params.putString("contest_name", contestName)
            params.putSerializable("contest_platform", platform)
            instance.logEvent("add_contest_to_google_calendar", params)
        }
    }

    override fun logRefreshingData(refreshScreen: RefreshScreen) {
        if (isEnabled) {
            instance.logEvent(when (refreshScreen) {
                RefreshScreen.USERS -> "users_list_refresh"
                RefreshScreen.CONTESTS -> "contests_list_refresh"
                RefreshScreen.NEWS -> "actions_list_refresh"
                RefreshScreen.PROBLEMS -> "problems_list_refresh"
            }, Bundle())
        }
    }

    override fun logUserAdded() {
        if (isEnabled) {
            instance.logEvent("user_added", Bundle())
        }
    }

    override fun logShareApp() {
        if (isEnabled) {
            instance.logEvent("actions_share_app", Bundle())
        }
    }

    override fun logAppShared() {
        if (isEnabled) {
            instance.logEvent("actions_app_shared", Bundle())
        }
    }

    override fun logShareComment() {
        if (isEnabled) {
            instance.logEvent("action_share_comment", Bundle())
        }
    }

    override fun logShareProblem() {
        if (isEnabled) {
            instance.logEvent("problem_shared", Bundle())
        }
    }

    override fun logActionOpened() {
        if (isEnabled) {
            instance.logEvent("action_opened", Bundle())
        }
    }

    override fun logProblemOpened() {
        if (isEnabled) {
            instance.logEvent("problem_opened", Bundle())
        }
    }

    override fun logPinnedPostOpened() {
        if (isEnabled) {
            instance.logEvent("actions_pinned_post_opened", Bundle())
        }
    }

    override fun logPinnedPostClosed() {
        if (isEnabled) {
            instance.logEvent("actions_pinned_post_closed", Bundle())
        }
    }

    override fun logFetchNews() {
        if (isEnabled) {
            instance.logEvent("news_fetch", Bundle())
        }
    }

    override fun logFetchNewsSuccess() {
        if (isEnabled) {
            instance.logEvent("news_fetch_success", Bundle())
        }
    }

    override fun logFetchNewsFailure() {
        if (isEnabled) {
            instance.logEvent("news_fetch_failure", Bundle())
        }
    }

    override fun logContestOpened() {
        if (isEnabled) {
            instance.logEvent("contest_opened", Bundle())
        }
    }

    override fun logContestShared() {
        if (isEnabled) {
            instance.logEvent("contest_shared", Bundle())
        }
    }
}
