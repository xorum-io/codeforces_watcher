package io.xorum.codeforceswatcher.features.problems.response

import io.xorum.codeforceswatcher.features.problems.models.Problem
import kotlinx.serialization.Serializable

@Serializable
internal data class ProblemsAndTagsResponse(
        val problems: List<Problem>,
        val tags: List<String>
)