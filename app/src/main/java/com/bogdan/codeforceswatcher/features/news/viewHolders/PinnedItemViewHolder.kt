package com.bogdan.codeforceswatcher.features.news.viewHolders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_pinned_action.view.*

class PinnedItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvTitle: TextView = view.tvTitle

    var onItemClickListener: (() -> Unit)? = null
    var onCrossClickListener: (() -> Unit)? = null

    init {
        view.run {
            setOnClickListener {
                onItemClickListener?.invoke()
            }
            ivCrossPost.setOnClickListener {
                onCrossClickListener?.invoke()
            }
        }
    }
}