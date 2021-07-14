package io.xorum.codeforceswatcher.features.contests.redux.requests

import io.xorum.codeforceswatcher.features.contests.ContestsRepository
import io.xorum.codeforceswatcher.features.contests.models.Contest
import io.xorum.codeforceswatcher.network.responses.backend.Response
import io.xorum.codeforceswatcher.redux.*
import tw.geothings.rekotlin.Action

class ContestsRequests {

    class FetchContests(
            private val isInitiatedByUser: Boolean
    ) : Request() {

        private val contestsRepository: ContestsRepository = ContestsRepository()

        override suspend fun execute() {
            val result = when (val response = contestsRepository.getAll()) {
                is Response.Success -> Success(response.result)
                is Response.Failure -> Failure(if (isInitiatedByUser) response.error.toMessage() else Message.None)
            }
            store.dispatch(result)
        }

        data class Success(val contests: List<Contest>) : Action
        data class Failure(override val message: Message) : ToastAction
    }

    class ChangeFilterCheckStatus(val platform: Contest.Platform, val isChecked: Boolean) : Action
}
