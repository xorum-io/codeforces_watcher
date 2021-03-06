package io.xorum.codeforceswatcher.features.problems

import io.ktor.client.request.*
import io.xorum.codeforceswatcher.features.problems.response.ProblemsAndTagsResponse
import io.xorum.codeforceswatcher.util.request

internal class ProblemsRepository {

    suspend fun getAll() = request { httpClient ->
        httpClient.get<ProblemsAndTagsResponse>(path = "problems")
    }
}