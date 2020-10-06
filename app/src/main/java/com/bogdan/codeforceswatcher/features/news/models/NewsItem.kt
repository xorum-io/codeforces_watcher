package com.bogdan.codeforceswatcher.features.news.models

import com.bogdan.codeforceswatcher.features.users.colorTextByUserRank
import com.bogdan.codeforceswatcher.features.users.getColorByUserRank
import com.bogdan.codeforceswatcher.util.convertFromHtml
import io.xorum.codeforceswatcher.network.responses.News
import io.xorum.codeforceswatcher.util.FeedUIModel

sealed class NewsItem {

    class PostWithCommentItem(post: News.Post, comment: News.Comment) : NewsItem() {

        val blogTitle: String = post.title.convertFromHtml()

        val postAuthorAvatar: String = post.author.avatar
        val postAuthorHandle: CharSequence = colorTextByUserRank(post.author.handle, post.author.rank)
        val postModifiedAt = post.modifiedAt
        val postContent = post.content.convertFromHtml()
        val postAuthorRankColor = getColorByUserRank(post.author.rank)

        val commentatorAvatar: String = comment.author.avatar
        val commentatorHandle: CharSequence = colorTextByUserRank(comment.author.handle, comment.author.rank)
        val commentCreatedAt = comment.createdAt
        val commentatorContent: String = comment.content.convertFromHtml()
        val commentLink = comment.link
        val commentatorRankColor = getColorByUserRank(comment.author.rank)
    }

    class PostItem(post: News.Post) : NewsItem() {

        val authorHandle: CharSequence = colorTextByUserRank(post.author.handle, post.author.rank)
        val blogTitle: String = post.title.convertFromHtml()
        val authorAvatar: String = post.author.avatar
        val modifiedAt: Long = post.modifiedAt
        val link = post.link
        val rankColor = getColorByUserRank(post.author.rank)
        val content = post.content.convertFromHtml()
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
