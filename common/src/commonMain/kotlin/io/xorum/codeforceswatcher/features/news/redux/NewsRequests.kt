package io.xorum.codeforceswatcher.features.news.redux

import io.xorum.codeforceswatcher.features.news.models.News
import io.xorum.codeforceswatcher.features.news.NewsRepository
import io.xorum.codeforceswatcher.network.responses.backend.Response
import io.xorum.codeforceswatcher.redux.*
import io.xorum.codeforceswatcher.util.settings
import tw.geothings.rekotlin.Action

class NewsRequests {

    class FetchNews(private val isInitiatedByUser: Boolean) : Request() {

        private val newsRepository = NewsRepository()

        override suspend fun execute() {
            when (val response = newsRepository.getNews()) {
                is Response.Success -> {
                    store.dispatch(Success(response.result.news))
                }
                is Response.Failure -> {
                    dispatchFailure()
                }
            }
        }

        private fun dispatchFailure() {
            val noConnectionError = if (isInitiatedByUser) Message.NoConnection else Message.None
            store.dispatch(Failure(noConnectionError))
        }

        data class Success(val news: List<News>) : Action

        data class Failure(override val message: Message) : ToastAction
    }

    class RemovePinnedPost(val link: String) : Request() {

        override suspend fun execute() = settings.writeLastPinnedPostLink(link)
    }
}
