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
import io.ktor.http.URLProtocol
import io.xorum.codeforceswatcher.network.responses.ContestResponse
import io.xorum.codeforceswatcher.redux.analyticsController
import io.xorum.codeforceswatcher.util.stringify
import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.builtins.list
import kotlinx.serialization.internal.UnitDescriptor
import kotlinx.serialization.json.Json.Default.nonstrict

private const val KONTESTS_API_LINK = "www.kontests.net/api/v1"

internal class KontestsRepository {

    private val kontestsApiClient = makeKontestsApiClient()

    suspend fun getAllContests() = try {
        kontestsApiClient.get<List<ContestResponse>>(path = "all")
    } catch (e: Exception) {
        analyticsController.logError(e.stringify())
        e.printStackTrace()

        null
    }

    @UseExperimental(UnstableDefault::class)
    private fun makeKontestsApiClient(): HttpClient = HttpClient {
        expectSuccess = false
        defaultRequest {
            url {
                host = KONTESTS_API_LINK
                protocol = URLProtocol.HTTPS
            }
        }
        Json {
            serializer = KotlinxSerializer(json = nonstrict).apply {
                register(ListContestSerializer)
            }
        }
        Logging {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }
}

private object ListContestSerializer : KSerializer<List<ContestResponse>> {

    override val descriptor: SerialDescriptor = UnitDescriptor

    override fun deserialize(decoder: Decoder) =
            ContestResponse.serializer().list.deserialize(decoder)

    override fun serialize(encoder: Encoder, obj: List<ContestResponse>) =
            ContestResponse.serializer().list.serialize(encoder, obj)
}

