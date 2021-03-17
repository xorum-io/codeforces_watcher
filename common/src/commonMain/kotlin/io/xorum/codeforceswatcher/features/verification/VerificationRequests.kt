package io.xorum.codeforceswatcher.features.verification

import io.xorum.codeforceswatcher.features.auth.UserAccount
import io.xorum.codeforceswatcher.network.VerificationRepository
import io.xorum.codeforceswatcher.network.responses.backend.Response
import io.xorum.codeforceswatcher.redux.*
import tw.geothings.rekotlin.Action

class VerificationRequests {

    class Verify(private val handle: String) : Request() {

        private val userAccountRepository = VerificationRepository(store.state.auth.token!!)

        override suspend fun execute() {
            when (val response = userAccountRepository.verifyCodeforcesAccount(handle)) {
                is Response.Success -> {
                    store.dispatch(Success(response.result))
                }
                is Response.Failure -> store.dispatch(Failure(response.error.toMessage()))
            }
        }

        data class Success(val userAccount: UserAccount) : Action
        data class Failure(override val message: Message) : ToastAction
    }

    class FetchVerificationCode : Request() {

        private val userAccountRepository = VerificationRepository(store.state.auth.token!!)

        override suspend fun execute() {
            val response = userAccountRepository.fetchCodeforcesVerificationCode()
            if (response is Response.Success) store.dispatch(Success(response.result.code))
        }

        data class Success(val verificationCode: String) : Action
    }
}
