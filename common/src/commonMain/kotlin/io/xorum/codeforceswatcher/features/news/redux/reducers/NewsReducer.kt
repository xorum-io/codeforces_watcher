package io.xorum.codeforceswatcher.features.news.redux.reducers

import io.xorum.codeforceswatcher.features.news.redux.requests.NewsRequests
import io.xorum.codeforceswatcher.features.news.redux.states.NewsState
import io.xorum.codeforceswatcher.network.responses.News
import io.xorum.codeforceswatcher.redux.states.AppState
import tw.geothings.rekotlin.Action

fun newsReducer(action: Action, state: AppState): NewsState {
    var newState = state.news

    when (action) {
        is NewsRequests.FetchNews.Success -> {
            newState = newState.copy(
                    news = action.news,
                    status = NewsState.Status.IDLE,
                    pinnedPost = action.pinnedPost
            )
        }
        is NewsRequests.FetchNews -> {
            newState = newState.copy(status = NewsState.Status.PENDING)
        }
        is NewsRequests.FetchNews.Failure -> {
            newState = newState.copy(status = NewsState.Status.IDLE)
        }
        is NewsRequests.RemovePinnedPost -> {
            newState = newState.copy(pinnedPost = null)
        }
    }

    return newState
}
