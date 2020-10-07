package com.bogdan.codeforceswatcher.features.news.models

import android.text.TextUtils
import com.bogdan.codeforceswatcher.features.users.colorTextByUserRank
import com.bogdan.codeforceswatcher.features.users.getColorByUserRank
import com.bogdan.codeforceswatcher.util.convertFromHtml
import io.xorum.codeforceswatcher.network.responses.News
import io.xorum.codeforceswatcher.util.FeedUIModel
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

sealed class NewsItem {

    class PostWithCommentItem(post: News.Post, comment: News.Comment) : NewsItem() {

        val blogTitle: String = post.title.convertFromHtml()

        val postAuthorAvatar: String = post.author.avatar
        val postContent = post.content.convertFromHtml()
        val postAuthorRankColor = getColorByUserRank(post.author.rank)
        val postAgoText = buildAgoText(post.author, post.modifiedAt)

        val commentatorAvatar: String = comment.author.avatar
        val commentContent: String = comment.content.convertFromHtml()
        val commentLink = comment.link
        val commentatorRankColor = getColorByUserRank(comment.author.rank)
        val commentAgoText = buildAgoText(comment.author, comment.createdAt)
    }

    class PostItem(post: News.Post) : NewsItem() {

        val blogTitle: String = post.title.convertFromHtml()
        val authorAvatar: String = post.author.avatar
        val link = post.link
        val rankColor = getColorByUserRank(post.author.rank)
        val content = post.content.convertFromHtml()
        val agoText = buildAgoText(post.author, post.modifiedAt)
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

private fun buildAgoText(user: News.User, time: Long): CharSequence {
    val handle: CharSequence = colorTextByUserRank(user.handle, user.rank)

    return TextUtils.concat(handle, " - ${PrettyTime().format(Date(time * 1000))}")
}