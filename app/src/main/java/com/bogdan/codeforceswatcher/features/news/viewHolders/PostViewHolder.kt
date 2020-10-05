package com.bogdan.codeforceswatcher.features.news.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_post_item.view.*

class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvHandleAndTime: TextView = view.tvHandleAndTime
    val tvTitle: TextView = view.tvTitle
    val tvContent: TextView = view.tvContent
    val ivAvatar: ImageView = view.ivAuthorAvatar

    var onItemClickListener: ((Int) -> Unit)? = null

    init {
        view.setOnClickListener {
            onItemClickListener?.invoke(adapterPosition)
        }
    }
}
