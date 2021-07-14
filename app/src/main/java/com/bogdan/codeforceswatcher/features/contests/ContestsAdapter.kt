package com.bogdan.codeforceswatcher.features.contests

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bogdan.codeforceswatcher.R
import io.xorum.codeforceswatcher.features.contests.models.Contest
import kotlinx.android.synthetic.main.view_contest_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class ContestsAdapter(
        private val context: Context,
        private val addToCalendarClickListener: (Contest) -> Unit,
        private val itemClickListener: (Contest) -> Unit
) : RecyclerView.Adapter<ContestsAdapter.ViewHolder>() {

    private var items: List<Contest> = listOf()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(context).inflate(R.layout.view_contest_item, parent, false)
        return ViewHolder(layout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contest = items[position]
        with(holder) {
            tvContestName.text = contest.title
            tvContestTime.text = getDateTime(contest.startDateInMillis)
            ivContest.setImageResource(when (contest.platform) {
                Contest.Platform.ATCODER -> R.drawable.atcoder
                Contest.Platform.TOPCODER -> R.drawable.topcoder
                Contest.Platform.CODEFORCES -> R.drawable.codeforces
                Contest.Platform.CODECHEF -> R.drawable.codechef
                Contest.Platform.CODEFORCES_GYM -> R.drawable.codeforces
                Contest.Platform.LEETCODE -> R.drawable.leetcode
                Contest.Platform.KICK_START -> R.drawable.kickstart
                Contest.Platform.HACKEREARTH -> R.drawable.hackerearth
                Contest.Platform.HACKERRANK -> R.drawable.hackerrank
                Contest.Platform.CS_ACADEMY -> R.drawable.csacademy
                Contest.Platform.TOPH -> R.drawable.toph
            })

            onAddToCalendarClickListener = { addToCalendarClickListener(contest) }
            onItemClickListener = { itemClickListener(contest) }
        }
    }

    fun setItems(contestList: List<Contest>) {
        items = contestList
        notifyDataSetChanged()
    }

    private fun getDateTime(seconds: Long): String {
        val dateFormat = SimpleDateFormat(context.getString(R.string.contest_date_format), Locale.getDefault())
        return dateFormat.format(Date(seconds)).toString()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvContestName: TextView = view.tvContestName
        val tvContestTime: TextView = view.tvContestTime
        val ivContest: ImageView = view.ivContest
        private val ivAddToCalendar: ImageView = view.ivAddToCalendar

        var onAddToCalendarClickListener: (() -> Unit)? = null
        var onItemClickListener: (() -> Unit)? = null

        init {
            ivAddToCalendar.setOnClickListener { onAddToCalendarClickListener?.invoke() }
            view.setOnClickListener { onItemClickListener?.invoke() }
        }
    }
}
