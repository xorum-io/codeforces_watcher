package com.bogdan.codeforceswatcher.features.news.models

import android.text.SpannableStringBuilder
import com.bogdan.codeforceswatcher.CwApp
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.features.users.colorTextByUserRank
import com.bogdan.codeforceswatcher.util.convertFromHtml
import io.xorum.codeforceswatcher.network.responses.News
import io.xorum.codeforceswatcher.util.FeedUIModel
import io.xorum.codeforceswatcher.util.avatar

sealed class NewsItem {

    class CommentItem(comment: News.Comment) : NewsItem() {

        val commentatorHandle: CharSequence = buildHandle(comment.author.handle, comment.author.rank)
        val title: String = comment.title.convertFromHtml()
        val content: String = comment.content.convertFromHtml()
        val commentatorAvatar: String = avatar(comment.author.avatar)
        val createdAt: Long = comment.createdAt
        val link = comment.link

        private fun buildHandle(handle: String, rank: String?): CharSequence {
            val colorHandle = colorTextByUserRank(handle, rank)
            val commentedByString = CwApp.app.getString(R.string.commented_by)
            val handlePosition = commentedByString.indexOf("%1\$s")

            return SpannableStringBuilder(commentedByString)
                    .replace(handlePosition, handlePosition + "%1\$s".length, colorHandle)
        }
    }

    class BlogEntryItem(post: News.Post) : NewsItem() {

        val authorHandle: CharSequence = colorTextByUserRank(post.author.handle, post.author.rank)
        val blogTitle: String = post.title.convertFromHtml()
        val authorAvatar: String = avatar(post.author.avatar)
        val createdAt: Long = post.createdAt
        val link = post.link
    }

    class PinnedItem(pinnedPost: News.PinnedPost) : NewsItem() {
        val title = pinnedPost.title
        val link = pinnedPost.link
    }

    class FeedbackItem(feedUIModel: FeedUIModel) : NewsItem() {
        val textPositiveButton = feedUIModel.textPositiveButton
        val textNegativeButton = feedUIModel.textNegativeButton
        val textTitle = feedUIModel.textTitle
        val positiveButtonClick = feedUIModel.positiveButtonClick
        val negativeButtonClick = feedUIModel.negativeButtonClick
        val neutralButtonClick = feedUIModel.neutralButtonClick
    }

    object Stub : NewsItem()
}
