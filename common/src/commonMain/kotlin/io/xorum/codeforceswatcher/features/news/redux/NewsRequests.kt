package io.xorum.codeforceswatcher.features.news.redux

import io.xorum.codeforceswatcher.features.news.models.News
import io.xorum.codeforceswatcher.features.news.NewsRepository
import io.xorum.codeforceswatcher.util.Response
import io.xorum.codeforceswatcher.redux.*
import io.xorum.codeforceswatcher.util.settings
import tw.geothings.rekotlin.Action

class NewsRequests {

    class FetchNews(private val isInitiatedByUser: Boolean) : Request() {

        private val newsRepository = NewsRepository()

        override suspend fun execute() {
            val result = when (val response = newsRepository.getNews()) {
                is Response.Success -> Success(response.result.news)
                is Response.Failure -> Failure(if (isInitiatedByUser) response.error.toMessage() else Message.None)
            }
            store.dispatch(result)
        }

        data class Success(val news: List<News>) : Action

        data class Failure(override val message: Message) : ToastAction
    }

    class RemovePinnedPost(val link: String) : Request() {

        override suspend fun execute() = settings.writeLastPinnedPostLink(link)
    }
}
