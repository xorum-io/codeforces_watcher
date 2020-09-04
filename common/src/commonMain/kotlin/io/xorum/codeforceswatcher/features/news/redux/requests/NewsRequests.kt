package io.xorum.codeforceswatcher.features.news.redux.requests

import io.xorum.codeforceswatcher.network.responses.News
import io.xorum.codeforceswatcher.redux.*
import io.xorum.codeforceswatcher.util.defineLang
import io.xorum.codeforceswatcher.util.settings
import tw.geothings.rekotlin.Action

class NewsRequests {

    class FetchNews(
            private val isInitializedByUser: Boolean,
            private val language: String
    ) : Request() {

        override suspend fun execute() {
            val response = backendApiClient.getNews(lang = language.defineLang())
            println(response)
            response?.news?.let { news ->
                val pinnedPost = news.find { it is News.PinnedPost } as News.PinnedPost
                store.dispatch((Success(news, pinnedPost)))
            } ?: dispatchFailure()
        }

        private fun dispatchFailure() {
            val noConnectionError = if (isInitializedByUser) Message.NoConnection else Message.None
            store.dispatch(Failure(noConnectionError))
        }

        data class Success(val news: List<News>, val pinnedPost: News.PinnedPost) : Action

        data class Failure(override val message: Message) : ToastAction
    }

    class RemovePinnedPost(val link: String) : Request() {
        override suspend fun execute() {
            settings.writeLastPinnedPostLink(link)
        }
    }
}
