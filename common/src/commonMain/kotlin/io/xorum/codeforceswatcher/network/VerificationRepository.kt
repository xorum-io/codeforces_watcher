package io.xorum.codeforceswatcher.network

import io.ktor.client.request.*
import io.xorum.codeforceswatcher.features.auth.UserAccount
import io.xorum.codeforceswatcher.features.verification.VerificationCodeResponse
import io.xorum.codeforceswatcher.network.responses.backend.KtorResponseClient

internal class VerificationRepository(
        private val token: String
) {

    private val ktorResponseClient
        get() = KtorResponseClient(token)

    suspend fun fetchCodeforcesVerificationCode() = ktorResponseClient.put<VerificationCodeResponse>("user/generate-verify-code/codeforces")

    suspend fun verifyCodeforcesAccount(handle: String) = ktorResponseClient.post<UserAccount>("user/verify/codeforces") {
        parameter("handle", handle)
    }
}