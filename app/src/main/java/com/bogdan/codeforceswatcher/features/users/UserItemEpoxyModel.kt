package com.bogdan.codeforceswatcher.features.users

import android.view.View
import androidx.core.content.ContextCompat
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.epoxy.BaseEpoxyModel
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.xorum.codeforceswatcher.util.avatar
import kotlinx.android.synthetic.main.view_user_item.view.*

data class UserItemEpoxyModel(
        private val userItem: UserItem
) : BaseEpoxyModel(R.layout.view_user_item) {

    init {
        id("UserItemEpoxyModel", userItem.toString())
    }

    override fun bind(view: View): Unit = with(view) {
        super.bind(view)
        Picasso.get().load(avatar(userItem.avatarLink))
                .placeholder(R.drawable.no_avatar)
                .into(ivAvatar)

        (ivAvatar as CircleImageView).borderColor = ContextCompat.getColor(context, userItem.rankColor)
        tvUserHandle.text = userItem.handle
        tvRating.text = userItem.rating
        tvDateLastRatingUpdate.text = userItem.dateOfLastRatingUpdate
        tvLastRatingUpdate.text = userItem.lastRatingUpdate
        showLastRatingUpdate(userItem.update, this)

        setOnClickListener {
            context.startActivity(UserActivity.newIntent(context, userItem.id))
        }
    }

    private fun showLastRatingUpdate(update: Update, view: View) = with(view) {
        when (update) {
            Update.NULL -> {
                ivDelta.setImageResource(0)
                tvLastRatingUpdate.text = null
            }
            Update.DECREASE -> {
                ivDelta.setImageResource(R.drawable.ic_rating_down)
                tvLastRatingUpdate.setTextColor(ContextCompat.getColor(context, R.color.red))
            }
            Update.INCREASE -> {
                ivDelta.setImageResource(R.drawable.ic_rating_up)
                tvLastRatingUpdate.setTextColor(ContextCompat.getColor(context, R.color.bright_green))
            }
        }
    }
}

class UsersStubItemEpoxyModel : BaseEpoxyModel(R.layout.view_users_stub) {

    init {
        id("UsersStubItemEpoxyModel")
    }
}
