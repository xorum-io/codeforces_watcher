package io.xorum.codeforceswatcher.features.verification

import io.ktor.client.request.*
import io.xorum.codeforceswatcher.features.auth.models.UserAccount
import io.xorum.codeforceswatcher.features.verification.response.VerificationCodeResponse
import io.xorum.codeforceswatcher.network.responses.backend.HttpClientFactory
import io.xorum.codeforceswatcher.network.responses.backend.request

internal class VerificationRepository(token: String) {

    private val ktorResponseClient = HttpClientFactory().create(token)

    suspend fun fetchCodeforcesVerificationCode() = request {
        ktorResponseClient.put<VerificationCodeResponse>("verify/generate-code/codeforces")
    }

    suspend fun verifyCodeforcesAccount(handle: String) = request {
        ktorResponseClient.post<UserAccount>("verify/codeforces") {
            parameter("platform_account", handle)
        }
    }
}