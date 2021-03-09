package io.xorum.codeforceswatcher.network.responses.kontests

import com.soywiz.klock.DateFormat
import com.soywiz.klock.parse
import io.xorum.codeforceswatcher.features.contests.models.Contest
import io.xorum.codeforceswatcher.features.contests.models.Platform
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContestResponse(
        val name: String,
        val url: String,
        @SerialName("start_time") val startTime: String,
        val duration: Double,
        val status: String,
        @SerialName("site") val platform: String
) {
    fun toContest(): Contest {
        val dateFormat = DateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        return Contest(0L, name, dateFormat.parse(startTime).local.unixMillisLong, duration.toLong() * 1000, status, parsePlatform(platform)).apply { link = url }
    }

    private fun parsePlatform(platform: String) = when (platform) {
        "CodeForces" -> Platform.CODEFORCES
        "CodeForces::Gym" -> Platform.CODEFORCES_GYM
        "TopCoder" -> Platform.TOPCODER
        "AtCoder" -> Platform.ATCODER
        "CS Academy" -> Platform.CS_ACADEMY
        "CodeChef" -> Platform.CODECHEF
        "HackerRank" -> Platform.HACKERRANK
        "HackerEarth" -> Platform.HACKEREARTH
        "Kick Start" -> Platform.KICK_START
        "LeetCode" -> Platform.LEETCODE
        "A2OJ" -> Platform.CODEFORCES
        else -> Platform.CODEFORCES
    }
}
