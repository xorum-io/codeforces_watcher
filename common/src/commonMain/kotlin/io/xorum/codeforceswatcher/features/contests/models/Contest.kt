package io.xorum.codeforceswatcher.features.contests.models

import kotlinx.serialization.Serializable

@Serializable
data class Contest(
        val platform: Platform,
        val title: String,
        val link: String,
        val startDateInMillis: Long,
        val durationInMillis: Long,
        val phase: Phase
) {

    @Serializable
    enum class Platform {
        CODEFORCES, CODEFORCES_GYM, TOPCODER, ATCODER, CS_ACADEMY,
        CODECHEF, HACKERRANK, HACKEREARTH, KICK_START, LEETCODE, TOPH;

        companion object {

            val defaultFilterValueToSave: Set<String>
                get() = values().map { it.toString() }.toSet()
        }
    }

    @Serializable
    enum class Phase {
        PENDING, RUNNING, FINISHED
    }
}
