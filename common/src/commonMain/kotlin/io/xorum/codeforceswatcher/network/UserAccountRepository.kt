package io.xorum.codeforceswatcher.network

import io.ktor.client.request.*
import io.xorum.codeforceswatcher.features.auth.UserAccount
import io.xorum.codeforceswatcher.features.users.models.User
import io.xorum.codeforceswatcher.features.verification.VerificationCodeResponse
import io.xorum.codeforceswatcher.network.responses.backend.KtorResponseClient
import io.xorum.codeforceswatcher.network.responses.backend.UserData

internal class UserAccountRepository(
        private val token: String
) {

    private val ktorResponseClient
        get() = KtorResponseClient(token)

    suspend fun fetchUserData(handles: List<User>) = ktorResponseClient.get<UserData>("user/fetch-user-data") {
        parameter("handles", handles.toHandlesString())
    }

    suspend fun fetchCodeforcesVerificationCode() = ktorResponseClient.put<VerificationCodeResponse>("user/generate-verify-code/codeforces")

    suspend fun verifyCodeforcesAccount(handle: String) = ktorResponseClient.post<UserAccount>("user/verify/codeforces") {
        parameter("handle", handle)
    }

    private fun List<User>.toHandlesString() = joinToString(",") { it.handle }
}