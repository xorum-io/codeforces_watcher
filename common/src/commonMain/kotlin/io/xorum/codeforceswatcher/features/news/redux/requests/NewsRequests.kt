package io.xorum.codeforceswatcher.features.news.redux.requests

import io.xorum.codeforceswatcher.features.news.News
import io.xorum.codeforceswatcher.network.responses.backend.Response
import io.xorum.codeforceswatcher.redux.*
import io.xorum.codeforceswatcher.util.AnalyticsEvents
import io.xorum.codeforceswatcher.util.defineLang
import io.xorum.codeforceswatcher.util.settings
import tw.geothings.rekotlin.Action

class NewsRequests {

    class FetchNews(
            private val isInitializedByUser: Boolean,
            private val language: String
    ) : Request() {

        override suspend fun execute() {
            analyticsController.logEvent(AnalyticsEvents.NEWS_FETCH)

            when (val response = backendRepository.getNews(lang = language.defineLang())) {
                is Response.Success -> {
                    analyticsController.logEvent(AnalyticsEvents.NEWS_FETCH_SUCCESS)
                    store.dispatch(Success(response.result.news))
                }
                is Response.Failure -> {
                    dispatchFailure()
                }
            }
        }

        private fun dispatchFailure() {
            val noConnectionError = if (isInitializedByUser) Message.NoConnection else Message.None
            store.dispatch(Failure(noConnectionError))
        }

        data class Success(val news: List<News>) : Action

        data class Failure(override val message: Message) : ToastAction
    }

    class RemovePinnedPost(val link: String) : Request() {

        override suspend fun execute() {
            analyticsController.logEvent(AnalyticsEvents.PINNED_POST_CLOSED)
            settings.writeLastPinnedPostLink(link)
        }
    }
}
