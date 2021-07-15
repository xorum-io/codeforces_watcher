package io.xorum.codeforceswatcher.features.verification

import io.ktor.client.request.*
import io.xorum.codeforceswatcher.features.auth.models.UserAccount
import io.xorum.codeforceswatcher.features.verification.response.VerificationCodeResponse
import io.xorum.codeforceswatcher.util.request

internal class VerificationRepository {

    suspend fun fetchCodeforcesVerificationCode() = request { httpClient ->
        httpClient.put<VerificationCodeResponse>("verify/generate-code/codeforces")
    }

    suspend fun verifyCodeforcesAccount(handle: String) = request { httpClient ->
        httpClient.post<UserAccount>("verify/codeforces") {
            parameter("platform_account", handle)
        }
    }
}