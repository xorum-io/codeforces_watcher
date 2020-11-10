package com.bogdan.codeforceswatcher.features.news.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_video_item.view.*

class VideoItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvHandleAndTime: TextView = view.tvHandleAndTime
    val tvTitle: TextView = view.tvTitle
    val ivThumbnail: ImageView = view.ivThumbnail
    val ivAvatar: ImageView = view.ivAuthorAvatar

    var onItemClickListener: ((Int) -> Unit)? = null

    init {
        view.setOnClickListener {
            onItemClickListener?.invoke(adapterPosition)
        }
    }
}