package io.xorum.codeforceswatcher.util

import io.xorum.codeforceswatcher.features.contests.models.Platform

enum class RefreshScreen { USERS, CONTESTS, NEWS, PROBLEMS }

interface IAnalyticsController {

    fun logAddContestToCalendarEvent(contestName: String, platform: Platform)

    fun logRefreshingData(refreshScreen: RefreshScreen)

    fun logUserAdded()

    fun logShareApp()

    fun logAppShared()

    fun logShareComment()

    fun logShareProblem()

    fun logActionOpened()

    fun logProblemOpened()

    fun logPinnedPostOpened()

    fun logPinnedPostClosed()

    fun logFetchNews()

    fun logFetchNewsSuccess()

    fun logFetchNewsFailure()

    fun logContestOpened()

    fun logContestShared()

    fun logError(message: String)
}