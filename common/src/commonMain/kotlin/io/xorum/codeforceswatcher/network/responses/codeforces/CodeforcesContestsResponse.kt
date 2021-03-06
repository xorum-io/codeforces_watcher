package io.xorum.codeforceswatcher.network.responses.codeforces

import io.xorum.codeforceswatcher.features.contests.models.Contest
import kotlinx.serialization.Serializable

@Serializable
data class CodeforcesContestsResponse(
        val status: String,
        val result: List<Contest>
)