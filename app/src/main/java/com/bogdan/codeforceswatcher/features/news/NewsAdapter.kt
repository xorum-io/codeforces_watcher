package com.bogdan.codeforceswatcher.features.news

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.features.news.models.NewsItem
import com.bogdan.codeforceswatcher.features.news.viewHolders.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.xorum.codeforceswatcher.features.news.redux.requests.NewsRequests
import io.xorum.codeforceswatcher.redux.analyticsController
import io.xorum.codeforceswatcher.redux.store
import java.lang.IllegalStateException

class NewsAdapter(
        private val context: Context,
        private val itemClickListener: (link: String, title: String, openEvent: String, shareEvent: String) -> Unit
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
                POST_WITH_COMMENT_VIEW_TYPE -> {
                    val layout = LayoutInflater.from(context).inflate(R.layout.view_post_with_comment_item, parent, false)
                    PostWithCommentViewHolder(layout)
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
                VIDEO_ITEM_VIEW_TYPE -> {
                    val layout = LayoutInflater.from(context).inflate(R.layout.view_video_item, parent, false)
                    VideoItemViewHolder(layout)
                }
                else -> throw IllegalStateException()
            }

    override fun getItemViewType(position: Int): Int {
        return when {
            items[position] is NewsItem.Stub -> STUB_VIEW_TYPE
            items[position] is NewsItem.PostWithCommentItem -> POST_WITH_COMMENT_VIEW_TYPE
            items[position] is NewsItem.PinnedItem -> PINNED_ITEM_VIEW_TYPE
            items[position] is NewsItem.FeedbackItem -> FEEDBACK_ITEM_VIEW_TYPE
            items[position] is NewsItem.PostItem -> POST_VIEW_TYPE
            items[position] is NewsItem.VideoItem -> VIDEO_ITEM_VIEW_TYPE
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is NewsItem.Stub -> return
            is NewsItem.PinnedItem -> bindPinnedItem(viewHolder as PinnedItemViewHolder, item)
            is NewsItem.PostWithCommentItem -> bindPostWithComment(viewHolder as PostWithCommentViewHolder, item)
            is NewsItem.PostItem -> bindPost(viewHolder as PostViewHolder, item)
            is NewsItem.FeedbackItem -> bindFeedbackItem(viewHolder as FeedbackItemViewHolder, item)
            is NewsItem.VideoItem -> bindVideoItem(viewHolder as VideoItemViewHolder, item)
        }
    }

    private fun bindPostWithComment(
            viewHolder: PostWithCommentViewHolder,
            postWithComment: NewsItem.PostWithCommentItem
    ) = with(postWithComment) {
        with(viewHolder) {
            tvTitle.text = blogTitle

            tvPostAuthorHandleAndTime.text = postAgoText
            tvPostContent.text = postContent

            tvCommentatorHandleAndTime.text = commentAgoText
            tvCommentContent.text = commentContent

            onCommentClickListener = {
                itemClickListener(commentLink, blogTitle, "action_opened", "action_share_comment")
            }

            onPostClickListener = {
                itemClickListener(postLink, blogTitle, "action_opened", "action_share_comment")
            }

            (ivPostAuthorAvatar as CircleImageView).borderColor = ContextCompat.getColor(context, postAuthorRankColor)
            (ivCommentatorAvatar as CircleImageView).borderColor = ContextCompat.getColor(context, commentatorRankColor)
        }

        Picasso.get().load(commentatorAvatar)
                .placeholder(R.drawable.no_avatar)
                .into(viewHolder.ivCommentatorAvatar)

        Picasso.get().load(postAuthorAvatar)
                .placeholder(R.drawable.no_avatar)
                .into(viewHolder.ivPostAuthorAvatar)
    }

    private fun bindPost(
            viewHolder: PostViewHolder,
            post: NewsItem.PostItem
    ) = with(post) {
        with(viewHolder) {
            tvTitle.text = blogTitle
            tvHandleAndTime.text = agoText
            tvContent.text = content
            onItemClickListener = {
                itemClickListener(link, blogTitle, "action_opened", "action_share_comment")
            }
            (ivAvatar as CircleImageView).borderColor = ContextCompat.getColor(context, rankColor)
        }

        Picasso.get().load(authorAvatar)
                .placeholder(R.drawable.no_avatar)
                .into(viewHolder.ivAvatar)
    }

    private fun bindPinnedItem(
            viewHolder: PinnedItemViewHolder,
            pinnedItem: NewsItem.PinnedItem
    ) = with(pinnedItem) {
        with(viewHolder) {
            tvTitle.text = title
            onItemClickListener = {
                itemClickListener(pinnedItem.link, pinnedItem.title, "actions_pinned_post_opened", "action_share_comment")
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

    private fun bindVideoItem(
            viewHolder: VideoItemViewHolder,
            videoItem: NewsItem.VideoItem
    ) = with(videoItem) {
        with(viewHolder) {
            tvTitle.text = title
            (ivAvatar as CircleImageView).borderColor = ContextCompat.getColor(context, rankColor)
            tvHandleAndTime.text = agoText

            onItemClickListener = {
                itemClickListener(link, title, "video_opened", "video_shared")
            }
        }

        Picasso.get().load(authorAvatar)
                .placeholder(R.drawable.no_avatar)
                .into(viewHolder.ivAvatar)

        Picasso.get().load(thumbnailLink)
                .placeholder(R.drawable.video_placeholder)
                .into(viewHolder.ivThumbnail)
    }

    fun setItems(actionsList: List<NewsItem>) {
        items = if (actionsList.isEmpty() || (actionsList.size == 1 && actionsList.first() is NewsItem.FeedbackItem)) listOf(NewsItem.Stub)
        else actionsList
        notifyDataSetChanged()
    }

    data class StubViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    companion object {
        const val STUB_VIEW_TYPE = 0
        const val POST_WITH_COMMENT_VIEW_TYPE = 1
        const val POST_VIEW_TYPE = 2
        const val PINNED_ITEM_VIEW_TYPE = 3
        const val FEEDBACK_ITEM_VIEW_TYPE = 4
        const val VIDEO_ITEM_VIEW_TYPE = 5
    }
}