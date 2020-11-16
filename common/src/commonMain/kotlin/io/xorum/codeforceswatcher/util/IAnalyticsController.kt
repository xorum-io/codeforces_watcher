package io.xorum.codeforceswatcher.util

interface IAnalyticsController {

    fun logError(message: String)

    fun logEvent(eventName: String, params: Map<String, String> = mapOf())
}