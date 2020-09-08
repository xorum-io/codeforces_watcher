package com.bogdan.codeforceswatcher.features.news

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bogdan.codeforceswatcher.CwApp
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.features.news.models.NewsItem
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.xorum.codeforceswatcher.features.news.redux.requests.NewsRequests
import io.xorum.codeforceswatcher.redux.analyticsController
import io.xorum.codeforceswatcher.redux.store
import kotlinx.android.synthetic.main.view_post_item.view.*
import kotlinx.android.synthetic.main.view_comment_item.view.*
import kotlinx.android.synthetic.main.view_comment_item.view.tvContent
import kotlinx.android.synthetic.main.view_comment_item.view.tvHandleAndTime
import kotlinx.android.synthetic.main.view_comment_item.view.tvTitle
import kotlinx.android.synthetic.main.view_feedback_card_view.view.*
import kotlinx.android.synthetic.main.view_pinned_action.view.*
import org.ocpsoft.prettytime.PrettyTime
import java.lang.IllegalStateException
import java.util.*

class NewsAdapter(
        private val context: Context,
        private val itemClickListener: (String, String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<NewsItem> = listOf()

    lateinit var callback: () -> Unit

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when (viewType) {
                STUB_VIEW_TYPE -> {
                    val layout = LayoutInflater.from(context).inflate(R.layout.view_news_stub, parent, false)
                    StubViewHolder(layout)
                }
                COMMENT_VIEW_TYPE -> {
                    val layout = LayoutInflater.from(context).inflate(R.layout.view_comment_item, parent, false)
                    CommentViewHolder(layout)
                }
                PINNED_ITEM_VIEW_TYPE -> {
                    val layout = LayoutInflater.from(context).inflate(R.layout.view_pinned_action, parent, false)
                    PinnedItemViewHolder(layout)
                }
                FEEDBACK_ITEM_VIEW_TYPE -> {
                    val layout = LayoutInflater.from(context).inflate(R.layout.view_feedback_card_view, parent, false)
                    FeedbackItemViewHolder(layout)
                }
                POST_VIEW_TYPE -> {
                    val layout = LayoutInflater.from(context).inflate(R.layout.view_post_item, parent, false)
                    PostViewHolder(layout)
                }
                else -> throw IllegalStateException()
            }

    override fun getItemViewType(position: Int): Int {
        return when {
            items[position] is NewsItem.Stub -> STUB_VIEW_TYPE
            items[position] is NewsItem.CommentItem -> COMMENT_VIEW_TYPE
            items[position] is NewsItem.PinnedItem -> PINNED_ITEM_VIEW_TYPE
            items[position] is NewsItem.FeedbackItem -> FEEDBACK_ITEM_VIEW_TYPE
            items[position] is NewsItem.PostItem -> POST_VIEW_TYPE
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is NewsItem.Stub -> return
            is NewsItem.PinnedItem -> bindPinnedItem(viewHolder as PinnedItemViewHolder, item)
            is NewsItem.CommentItem -> bindComment(viewHolder as CommentViewHolder, item)
            is NewsItem.PostItem -> bindPost(viewHolder as PostViewHolder, item)
            is NewsItem.FeedbackItem -> bindFeedbackItem(viewHolder as FeedbackItemViewHolder, item)
        }
    }

    private fun bindComment(viewHolder: CommentViewHolder, comment: NewsItem.CommentItem) = with(comment) {
        with(viewHolder) {
            tvTitle.text = title
            tvHandleAndTime.text = TextUtils.concat(commentatorHandle, " - ${PrettyTime().format(Date(createdAt * 1000))}")
            tvContent.text = content
            onItemClickListener = {
                itemClickListener(comment.link, comment.title)
            }
            (ivAvatar as CircleImageView).borderColor = ContextCompat.getColor(context, comment.rankColor)
        }

        Picasso.get().load(commentatorAvatar)
                .placeholder(R.drawable.no_avatar)
                .into(viewHolder.ivAvatar)
    }

    private fun bindPost(viewHolder: PostViewHolder, post: NewsItem.PostItem) = with(post) {
        with(viewHolder) {
            tvTitle.text = blogTitle
            tvHandleAndTime.text = TextUtils.concat(authorHandle, " - ${PrettyTime().format(Date(createdAt * 1000))}")
            tvContent.text = CwApp.app.getString(R.string.created_or_updated_text)
            onItemClickListener = {
                itemClickListener(post.link, post.blogTitle)
            }
            (ivAvatar as CircleImageView).borderColor = ContextCompat.getColor(context, post.rankColor)
        }

        Picasso.get().load(authorAvatar)
                .placeholder(R.drawable.no_avatar)
                .into(viewHolder.ivAvatar)
    }

    private fun bindPinnedItem(viewHolder: PinnedItemViewHolder, pinnedItem: NewsItem.PinnedItem) = with(pinnedItem) {
        with(viewHolder) {
            tvTitle.text = title
            onItemClickListener = {
                itemClickListener(pinnedItem.link, pinnedItem.title)
                analyticsController.logPinnedPostOpened()
            }
            onCrossClickListener = {
                store.dispatch(NewsRequests.RemovePinnedPost(pinnedItem.link))
            }
        }
    }

    private fun bindFeedbackItem(
            viewHolder: FeedbackItemViewHolder,
            feedbackItem: NewsItem.FeedbackItem
    ) = with(feedbackItem) {
        with(viewHolder) {
            tvTitle.text = textTitle

            btnNegative.text = textNegativeButton
            btnPositive.text = textPositiveButton

            onCrossClickListener = {
                neutralButtonClick.invoke()
                callback.invoke()
            }

            onNegativeBtnClickListener = {
                negativeButtonClick.invoke()
                callback.invoke()
            }

            onPositiveBtnClickListener = {
                positiveButtonClick.invoke()
                callback.invoke()
            }
        }
    }

    fun setItems(actionsList: List<NewsItem>) {
        items = if (actionsList.isEmpty() || (actionsList.size == 1 && (actionsList.first() is NewsItem.PinnedItem ||
                        actionsList.first() is NewsItem.FeedbackItem))) listOf(NewsItem.Stub)
        else actionsList
        notifyDataSetChanged()
    }

    class FeedbackItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.tvTitle
        val btnPositive: Button = view.btnPositive
        val btnNegative: Button = view.btnNegative

        var onCrossClickListener: (() -> Unit)? = null
        var onNegativeBtnClickListener: (() -> Unit)? = null
        var onPositiveBtnClickListener: (() -> Unit)? = null

        init {
            view.run {
                ivCrossFeedback.setOnClickListener {
                    onCrossClickListener?.invoke()
                }

                btnNegative.setOnClickListener {
                    onNegativeBtnClickListener?.invoke()
                }

                btnPositive.setOnClickListener {
                    onPositiveBtnClickListener?.invoke()
                }
            }
        }
    }

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

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHandleAndTime: TextView = view.tvHandleAndTime
        val tvTitle: TextView = view.tvTitle
        val tvContent: TextView = view.tvContent
        val ivAvatar: ImageView = view.ivCommentatorAvatar

        var onItemClickListener: ((Int) -> Unit)? = null

        init {
            view.setOnClickListener {
                onItemClickListener?.invoke(adapterPosition)
            }
        }
    }

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

    data class StubViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    companion object {
        const val STUB_VIEW_TYPE = 0
        const val COMMENT_VIEW_TYPE = 1
        const val POST_VIEW_TYPE = 2
        const val PINNED_ITEM_VIEW_TYPE = 3
        const val FEEDBACK_ITEM_VIEW_TYPE = 4
    }
}