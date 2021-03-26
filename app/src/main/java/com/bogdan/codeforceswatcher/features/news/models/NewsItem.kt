package com.bogdan.codeforceswatcher.features.news.models

import android.text.TextUtils
import com.bogdan.codeforceswatcher.CwApp
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.features.users.colorTextByUserRank
import com.bogdan.codeforceswatcher.features.users.getColorByUserRank
import io.xorum.codeforceswatcher.features.news.models.News
import io.xorum.codeforceswatcher.util.FeedUIModel
import org.ocpsoft.prettytime.PrettyTime
import java.util.*

sealed class NewsItem {

    class PostWithCommentItem(post: News.Post, comment: News.Comment) : NewsItem() {

        val blogTitle = post.title

        val postAuthorAvatar = post.author.avatar
        val postContent = post.content
        val postLink = post.link
        val postAuthorRankColor = getColorByUserRank(post.author.rank)
        val postAgoText = buildPostAgoText(post.author, post.modifiedAt, post.isModified)

        val commentatorAvatar = comment.author.avatar
        val commentContent = comment.content
        val commentLink = comment.link
        val commentatorRankColor = getColorByUserRank(comment.author.rank)
        val commentAgoText = buildAgoText(comment.author, comment.createdAt)
    }

    class PostItem(post: News.Post) : NewsItem() {

        val blogTitle = post.title
        val authorAvatar = post.author.avatar
        val link = post.link
        val rankColor = getColorByUserRank(post.author.rank)
        val content = post.content
        val agoText = buildPostAgoText(post.author, post.modifiedAt, post.isModified)
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

    class VideoItem(video: News.Video) : NewsItem() {

        val authorAvatar = video.author.avatar
        val rankColor = getColorByUserRank(video.author.rank)
        val title = video.title
        val agoText = buildAgoText(video.author, video.createdAt)
        val thumbnailLink = video.thumbnailLink
        val link = video.link
    }

    object Stub : NewsItem()
}

private fun buildAgoText(user: News.User, time: Long): CharSequence {
    val handle: CharSequence = colorTextByUserRank(user.handle, user.rank)

    return TextUtils.concat(handle, " - ${PrettyTime().format(Date(time * 1000))}")
}

private fun buildPostAgoText(user: News.User, time: Long, isModified: Boolean): CharSequence {
    val handle: CharSequence = colorTextByUserRank(user.handle, user.rank)
    val postState = CwApp.app.getString(if (isModified) R.string.modified else R.string.created)

    return TextUtils.concat(handle, " - $postState ${PrettyTime().format(Date(time * 1000))}")
}
