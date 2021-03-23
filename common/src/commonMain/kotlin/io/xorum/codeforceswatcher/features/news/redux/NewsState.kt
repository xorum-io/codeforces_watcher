package io.xorum.codeforceswatcher.features.news.redux

import io.xorum.codeforceswatcher.features.news.News
import tw.geothings.rekotlin.StateType

data class NewsState(
        val status: Status = Status.IDLE,
        val news: List<News> = listOf()
) : StateType {

    enum class Status { IDLE, PENDING }
}
