package com.bogdan.codeforceswatcher.features.news.viewHolders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_post_with_comment_item.view.*

class PostWithCommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvPostAuthorHandleAndTime: TextView = view.tvPostHandleAndTime
    val tvTitle: TextView = view.tvTitle
    val ivPostAuthorAvatar: ImageView = view.ivPostAuthorAvatar
    val tvPostContent: TextView = view.tvPostContent

    val tvCommentatorHandleAndTime: TextView = view.tvCommentHandleAndTime
    val ivCommentatorAvatar: ImageView = view.ivCommentAuthorAvatar
    val tvCommentContent: TextView = view.tvCommentContent

    var onCommentClickListener: (() -> Unit)? = null
    var onPostClickListener: (() -> Unit)? = null

    init {
        view.postContainer.setOnClickListener {
            onPostClickListener?.invoke()
        }
        view.commentContainer.setOnClickListener {
            onCommentClickListener?.invoke()
        }
        view.footerContainer.setOnClickListener {
            onPostClickListener?.invoke()
        }
    }
}
