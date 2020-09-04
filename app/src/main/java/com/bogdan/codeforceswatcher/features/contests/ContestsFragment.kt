package com.bogdan.codeforceswatcher.features.contests

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.features.news.WebViewActivity
import com.bogdan.codeforceswatcher.util.Analytics
import com.bogdan.codeforceswatcher.util.Refresh
import io.xorum.codeforceswatcher.features.contests.models.Contest
import io.xorum.codeforceswatcher.features.contests.redux.requests.ContestsRequests
import io.xorum.codeforceswatcher.features.contests.redux.states.ContestsState
import io.xorum.codeforceswatcher.redux.store
import kotlinx.android.synthetic.main.fragment_contests.*
import tw.geothings.rekotlin.StoreSubscriber
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

class ContestsFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, StoreSubscriber<ContestsState> {

    private val contestsAdapter by lazy {
        ContestsAdapter(
                requireContext(),
                addToCalendarClickListener = { addContestToCalendar(it) },
                itemClickListener = { startActivity(WebViewActivity.newIntent(requireContext(), it.link, it.name)) }
        )
    }

    override fun onStart() {
        super.onStart()
        store.subscribe(this) { state ->
            state.skipRepeats { oldState, newState ->
                oldState.contests == newState.contests
            }.select { it.contests }
        }
    }

    override fun onStop() {
        super.onStop()
        store.unsubscribe(this)
    }

    override fun onNewState(state: ContestsState) {
        swipeRefreshLayout.isRefreshing = (state.status == ContestsState.Status.PENDING)
        val showingContests = state.contests.filter { it.phase == "BEFORE" }.sortedBy(Contest::startTimeSeconds).filter { state.filters.contains(it.platform) }
        contestsAdapter.setItems(showingContests)
        if (showingContests.isEmpty()) {
            tvNoContest.visibility = View.VISIBLE
        } else {
            tvNoContest.visibility = View.GONE
        }
    }

    override fun onRefresh() {
        store.dispatch(ContestsRequests.FetchContests(isInitiatedByUser = true))
        Analytics.logRefreshingData(Refresh.CONTESTS)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_contests, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        swipeRefreshLayout.setOnRefreshListener(this)
        recyclerView.adapter = contestsAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun addContestToCalendar(contest: Contest) {
        val timeStart = getCalendarTime(contest.startTimeSeconds)
        val timeEnd = getCalendarTime(contest.startTimeSeconds + contest.durationSeconds)
        val encodeName = URLEncoder.encode(contest.name)
        val calendarEventLink =
                "${CALENDAR_LINK}?action=TEMPLATE&text=$encodeName&dates=$timeStart/$timeEnd&details=${contest.link}"
        val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(calendarEventLink))
        try {
            context?.startActivity(intent)
        } catch (error: ActivityNotFoundException) {
            Toast.makeText(
                    context,
                    context?.resources?.getString(R.string.google_calendar_not_found),
                    Toast.LENGTH_SHORT
            ).show()
        }
        Analytics.logAddContestToCalendarEvent(contest.name, contest.platform)
    }

    private fun getCalendarTime(time: Long): String {
        val dateFormat = SimpleDateFormat("yyyyMMd'T'HHmmss", Locale.getDefault())
        return dateFormat.format(Date(time)).toString()
    }

    companion object {
        private const val CALENDAR_LINK = "https://calendar.google.com/calendar/render"
    }
}
