package com.bogdan.codeforceswatcher.features.actions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.features.actions.models.ActionItem
import com.bogdan.codeforceswatcher.util.Analytics
import com.bogdan.codeforceswatcher.util.Refresh
import io.xorum.codeforceswatcher.features.actions.models.CFAction
import io.xorum.codeforceswatcher.features.actions.redux.requests.ActionsRequests
import io.xorum.codeforceswatcher.features.actions.redux.states.ActionsState
import io.xorum.codeforceswatcher.redux.store
import io.xorum.codeforceswatcher.util.settings
import kotlinx.android.synthetic.main.fragment_users.*
import tw.geothings.rekotlin.StoreSubscriber
import java.util.*

class ActionsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, StoreSubscriber<ActionsState> {

    private lateinit var actionsAdapter: ActionsAdapter

    override fun onStart() {
        super.onStart()
        store.subscribe(this) { state ->
            state.skipRepeats { oldState, newState ->
                oldState.actions == newState.actions
            }.select { it.actions }
        }
    }

    override fun onStop() {
        super.onStop()
        store.unsubscribe(this)
    }

    private fun buildActionItems(actions: List<CFAction>) =
            actions.map {
                if (it.comment != null) {
                    ActionItem.CommentItem(it)
                } else {
                    ActionItem.BlogEntryItem(it)
                }
            }

    override fun onNewState(state: ActionsState) {
        if (state.status == ActionsState.Status.PENDING) {
            swipeRefreshLayout.isRefreshing = true
        } else {
            swipeRefreshLayout.isRefreshing = false
            val items = mutableListOf<ActionItem>()
            state.pinnedPost?.let {
                if (settings.readPinnedPostLink() != it.link) items.add(ActionItem.PinnedItem(it))
            }
            items.addAll(buildActionItems(state.actions))
            actionsAdapter.setItems(items)
        }
    }

    override fun onRefresh() {
        store.dispatch(ActionsRequests.FetchActions(true, Locale.getDefault().language))
        store.dispatch(ActionsRequests.FetchPinnedPost())
        Analytics.logRefreshingData(Refresh.ACTIONS)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_actions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        swipeRefreshLayout.setOnRefreshListener(this)
        actionsAdapter = ActionsAdapter(requireContext()) { link, title ->
            startActivity(WebViewActivity.newIntent(requireContext(), link, title))
        }
        recyclerView.adapter = actionsAdapter
    }
}
