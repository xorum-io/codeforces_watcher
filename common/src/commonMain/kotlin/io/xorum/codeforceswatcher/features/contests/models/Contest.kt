package io.xorum.codeforceswatcher.features.contests.models

import io.xorum.codeforceswatcher.DbContest
import kotlinx.serialization.Serializable

enum class Platform {
    CODEFORCES, CODEFORCES_GYM, TOPCODER, ATCODER, CS_ACADEMY, CODECHEF, HACKERRANK, HACKEREARTH, KICK_START, LEETCODE;

    companion object {

        val defaultFilterValueToSave: Set<String>
            get() = values().map { it.toString() }.toSet()
    }
}

@Serializable
data class Contest(
        val id: Long,
        val name: String,
        val startTimeSeconds: Long,
        val durationSeconds: Long,
        val phase: String,
        val platform: Platform = Platform.CODEFORCES
) {
    var link: String = ""
        get() = field.takeIf { it.isNotEmpty() } ?: "$CODEFORCES_CONTESTS_LINK/$id"

    companion object {
        fun fromDB(dbContest: DbContest) = Contest(
                id = dbContest.id,
                name = dbContest.name,
                startTimeSeconds = dbContest.time,
                durationSeconds = dbContest.duration,
                phase = dbContest.phase
        )

        private const val CODEFORCES_CONTESTS_LINK = "https://codeforces.com/contests"
    }
}
