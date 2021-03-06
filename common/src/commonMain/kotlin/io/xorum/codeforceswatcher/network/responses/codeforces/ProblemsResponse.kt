package io.xorum.codeforceswatcher.network.responses.codeforces

import io.xorum.codeforceswatcher.features.problems.models.Problem
import kotlinx.serialization.Serializable

@Serializable
data class ProblemsResponse(
        val status: String,
        val result: Result
)

@Serializable
data class Result(val problems: List<Problem>)