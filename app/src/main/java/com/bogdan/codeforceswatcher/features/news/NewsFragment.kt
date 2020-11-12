package com.bogdan.codeforceswatcher.features.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.features.news.models.NewsItem
import com.bogdan.codeforceswatcher.util.FeedbackController
import io.xorum.codeforceswatcher.features.news.redux.requests.NewsRequests
import io.xorum.codeforceswatcher.features.news.redux.states.NewsState
import io.xorum.codeforceswatcher.network.responses.News
import io.xorum.codeforceswatcher.redux.analyticsController
import io.xorum.codeforceswatcher.redux.store
import io.xorum.codeforceswatcher.util.settings
import kotlinx.android.synthetic.main.fragment_users.*
import tw.geothings.rekotlin.StoreSubscriber
import java.util.*

class NewsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, StoreSubscriber<NewsState> {

    private lateinit var newsAdapter: NewsAdapter

    override fun onStart() {
        super.onStart()
        store.subscribe(this) { state ->
            state.skipRepeats { oldState, newState ->
                oldState.news == newState.news
            }.select { it.news }
        }
    }

    override fun onStop() {
        super.onStop()
        store.unsubscribe(this)
    }

    private fun buildNewsItems(news: List<News>) = news.map {
        when (it) {
            is News.Post -> NewsItem.PostItem(it)
            is News.PinnedPost -> NewsItem.PinnedItem(it)
            is News.PostWithComment -> NewsItem.PostWithCommentItem(it.post, it.comment)
            is News.Video -> NewsItem.VideoItem(it)
        }
    }

    override fun onNewState(state: NewsState) {
        if (state.status == NewsState.Status.PENDING) {
            swipeRefreshLayout.isRefreshing = true
        } else {
            swipeRefreshLayout.isRefreshing = false
            val items = mutableListOf<NewsItem>()

            val feedbackController = FeedbackController.get()

            if (feedbackController.shouldShowFeedbackCell()) {
                items.add(NewsItem.FeedbackItem(feedbackController.feedUIModel))
                newsAdapter.callback = {
                    onNewState(state)
                }
            }

            val news = state.news.filter {
                return@filter if (it is News.PinnedPost) settings.readLastPinnedPostLink() != it.link else true
            }

            val newsItems = buildNewsItems(news)
            items.addAll(newsItems)

            newsAdapter.setItems(items)
        }
    }

    override fun onRefresh() {
        store.dispatch(NewsRequests.FetchNews(true, Locale.getDefault().language))
        analyticsController.logEvent("actions_list_refresh")
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_news, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        swipeRefreshLayout.setOnRefreshListener(this)
        newsAdapter = NewsAdapter(requireContext()) { link, title, openEvent, shareEvent ->
            startActivity(WebViewActivity.newIntent(requireContext(), link, title, openEvent, shareEvent))
        }
        recyclerView.adapter = newsAdapter
    }
}
