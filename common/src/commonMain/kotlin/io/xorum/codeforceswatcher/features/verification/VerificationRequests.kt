package io.xorum.codeforceswatcher.features.verification

import io.xorum.codeforceswatcher.features.auth.UserAccount
import io.xorum.codeforceswatcher.network.responses.Response
import io.xorum.codeforceswatcher.redux.*
import io.xorum.codeforceswatcher.util.settings
import tw.geothings.rekotlin.Action

class VerificationRequests {

    class Verify(private val handle: String) : Request() {

        override suspend fun execute() {
            when (val response = backendRepository.verifyCodeforcesAccount(handle)) {
                is Response.Success -> {
                    store.dispatch(Success(response.result))
                    settings.writeUserAccount(response.result)
                }
                is Response.Failure -> store.dispatch(Failure(response.error.toMessage()))
            }
        }

        data class Success(val userAccount: UserAccount) : Action
        data class Failure(override val message: Message) : ToastAction
    }

    class FetchVerificationCode : Request() {

        override suspend fun execute() {
            val response = backendRepository.fetchCodeforcesVerificationCode()
            if (response is Response.Success) store.dispatch(Success(response.result.code))
        }

        data class Success(val verificationCode: String) : Action
    }
}
