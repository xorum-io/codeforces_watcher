package com.bogdan.codeforceswatcher.features.users

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.R.color.bright_green
import com.bogdan.codeforceswatcher.R.color.red
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.xorum.codeforceswatcher.util.avatar
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.view_user_item.view.*

class UsersAdapter(
        private val context: Context,
        private val itemClickListener: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<UserItem> = listOf()

    override fun getItemCount() = items.size

    fun setItems(userList: List<UserItem>) {
        items = if (userList.isEmpty()) listOf(UserItem.Stub) else userList
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is UserItem.Stub) STUB_VIEW_TYPE
        else USER_VIEW_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            when (viewType) {
                STUB_VIEW_TYPE -> {
                    val layout = LayoutInflater.from(context).inflate(R.layout.view_users_stub, parent, false)
                    StubViewHolder(layout)
                }
                else -> {
                    val layout = LayoutInflater.from(context).inflate(R.layout.view_user_item, parent, false)
                    UserViewHolder(layout, itemClickListener)
                }
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (items[position] is UserItem.Stub) return

        with(items[position] as UserItem.User) {
            with(holder as UserViewHolder) {
                Picasso.get().load(avatar(avatarLink))
                        .placeholder(R.drawable.no_avatar)
                        .into(ivAvatar)

                (ivAvatar as CircleImageView).borderColor = ContextCompat.getColor(context, rankColor)
                tvHandle.text = handle
                tvRating.text = rating
                tvDateLastRatingUpdate.text = dateOfLastRatingUpdate
                tvLastRatingUpdate.text = lastRatingUpdate
                showLastRatingUpdate(update, holder)
            }
        }
    }

    private fun showLastRatingUpdate(update: Update, holder: UserViewHolder) = with(holder) {
        when (update) {
            Update.NULL -> {
                ivDelta.setImageResource(0)
                tvLastRatingUpdate.text = null
            }
            Update.DECREASE -> {
                ivDelta.setImageResource(R.drawable.ic_rating_down)
                tvLastRatingUpdate.setTextColor(ContextCompat.getColor(context, red))
            }
            Update.INCREASE -> {
                ivDelta.setImageResource(R.drawable.ic_rating_up)
                tvLastRatingUpdate.setTextColor(ContextCompat.getColor(context, bright_green))
            }
        }
    }

    class UserViewHolder(view: View, itemClickListener: (Int) -> Unit) : RecyclerView.ViewHolder(view) {
        val ivAvatar: ImageView = view.ivAvatar
        val tvHandle: TextView = view.tvUserHandle
        val tvRating: TextView = view.tvRating
        val tvDateLastRatingUpdate: TextView = view.tvDateLastRatingUpdate
        val tvLastRatingUpdate: TextView = view.tvLastRatingUpdate
        val ivDelta: ImageView = view.ivDelta

        init {
            view.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) itemClickListener.invoke(adapterPosition)
            }
        }
    }

    data class StubViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    companion object {
        const val USER_VIEW_TYPE = 1
        const val STUB_VIEW_TYPE = 0
    }
}
