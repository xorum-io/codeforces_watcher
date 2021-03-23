package io.xorum.codeforceswatcher.features.news.redux

import io.xorum.codeforceswatcher.features.news.models.News
import io.xorum.codeforceswatcher.redux.states.AppState
import tw.geothings.rekotlin.Action

fun newsReducer(action: Action, state: AppState): NewsState {
    var newState = state.news

    when (action) {
        is NewsRequests.FetchNews.Success -> {
            newState = newState.copy(
                    news = action.news,
                    status = NewsState.Status.IDLE
            )
        }
        is NewsRequests.FetchNews -> {
            newState = newState.copy(status = NewsState.Status.PENDING)
        }
        is NewsRequests.FetchNews.Failure -> {
            newState = newState.copy(status = NewsState.Status.IDLE)
        }
        is NewsRequests.RemovePinnedPost -> {
            newState = newState.copy(news = state.news.news.filterNot { it is News.PinnedPost })
        }
    }

    return newState
}
