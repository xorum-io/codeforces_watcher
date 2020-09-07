package io.xorum.codeforceswatcher.features.news.redux.states

import io.xorum.codeforceswatcher.network.responses.News
import tw.geothings.rekotlin.StateType

data class NewsState(
        val status: Status = Status.IDLE,
        val news: List<News> = listOf()
) : StateType {

    enum class Status { IDLE, PENDING }
}
