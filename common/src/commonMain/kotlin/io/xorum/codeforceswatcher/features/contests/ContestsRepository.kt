package io.xorum.codeforceswatcher.features.contests

import io.ktor.client.request.*
import io.xorum.codeforceswatcher.features.contests.models.Contest
import io.xorum.codeforceswatcher.util.request

internal class ContestsRepository {

    suspend fun getAll() = request { httpClient ->
        httpClient.get<List<Contest>>(path = "contests")
    }
}