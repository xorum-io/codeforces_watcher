package io.xorum.codeforceswatcher.network

import io.ktor.client.HttpClient
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.Json
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.URLProtocol
import io.xorum.codeforceswatcher.network.responses.codeforces.CodeforcesContestsResponse
import io.xorum.codeforceswatcher.network.responses.codeforces.ProblemsResponse
import io.xorum.codeforceswatcher.redux.analyticsController
import io.xorum.codeforceswatcher.util.stringify

private const val CODEFORCES_API_LINK = "www.codeforces.com/api"

internal class CodeforcesRepository {

    private val codeforcesApiClient = makeCodeforcesApiClient()

    suspend fun getCodeforcesContests(lang: String) = try {
        codeforcesApiClient.get<CodeforcesContestsResponse>(path = "contest.list") {
            parameter("lang", lang)
        }
    } catch (e: Throwable) {
        analyticsController.logError(e.stringify())
        e.printStackTrace()

        null
    }

    suspend fun getProblems(lang: String) = try {
        codeforcesApiClient.get<ProblemsResponse>(path = "problemset.problems") {
            parameter("lang", lang)
        }
    } catch (e: Throwable) {
        analyticsController.logError(e.stringify())
        e.printStackTrace()

        null
    }

    private fun makeCodeforcesApiClient(): HttpClient = HttpClient {
        expectSuccess = false
        defaultRequest {
            url {
                host = CODEFORCES_API_LINK
                protocol = URLProtocol.HTTPS
            }
        }
        Json {
            serializer = KotlinxSerializer(
                kotlinx.serialization.json.Json(from = kotlinx.serialization.json.Json.Default) {
                    isLenient = true
                    ignoreUnknownKeys = true
                    allowSpecialFloatingPointValues = true
                    useArrayPolymorphism = true
                    encodeDefaults = true
                }
            )
        }
        Logging {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }
}
