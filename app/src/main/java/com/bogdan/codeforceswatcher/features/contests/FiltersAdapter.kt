package com.bogdan.codeforceswatcher.features.contests

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bogdan.codeforceswatcher.R
import io.xorum.codeforceswatcher.features.contests.models.Contest
import io.xorum.codeforceswatcher.features.contests.redux.requests.ContestsRequests
import io.xorum.codeforceswatcher.redux.store
import kotlinx.android.synthetic.main.view_contest_item.view.ivContest
import kotlinx.android.synthetic.main.view_filter_item.view.*

data class FilterItem(
        val imageId: Int,
        val title: String,
        val platform: Contest.Platform,
        val isChecked: Boolean
)

class FiltersAdapter(
        private val context: Context,
        private var items: List<FilterItem> = listOf()
) : RecyclerView.Adapter<FiltersAdapter.ViewHolder>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.view_filter_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val filterItem = items[position]
        with(holder) {
            title.text = filterItem.title
            ivContest.setImageResource(filterItem.imageId)
            checkBox.isChecked = filterItem.isChecked
            onCheckedChangeListener = { isChecked ->
                store.dispatch(ContestsRequests.ChangeFilterCheckStatus(filterItem.platform, isChecked))
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title: TextView = view.tvTitle
        val ivContest: ImageView = view.ivContest
        val checkBox: CheckBox = view.checkbox

        var onCheckedChangeListener: ((Boolean) -> Unit)? = null

        init {
            checkBox.setOnCheckedChangeListener { _, isChecked -> onCheckedChangeListener?.invoke(isChecked) }
        }
    }
}
