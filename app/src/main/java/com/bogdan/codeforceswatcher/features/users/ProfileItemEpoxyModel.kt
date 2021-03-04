package com.bogdan.codeforceswatcher.features.users

import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.epoxy.BaseEpoxyModel
import com.bogdan.codeforceswatcher.features.auth.SignInActivity
import com.bogdan.codeforceswatcher.features.auth.VerificationActivity
import com.bogdan.codeforceswatcher.util.colorSubstring
import com.squareup.picasso.Picasso
import io.xorum.codeforceswatcher.features.auth.AuthRequests
import io.xorum.codeforceswatcher.features.auth.AuthState
import io.xorum.codeforceswatcher.features.auth.UserAccount
import io.xorum.codeforceswatcher.features.users.models.User
import io.xorum.codeforceswatcher.redux.store
import kotlinx.android.synthetic.main.no_user_card_layout.view.*
import kotlinx.android.synthetic.main.user_profile_layout.view.*
import kotlinx.android.synthetic.main.view_profile_item.view.*

class ProfileItemEpoxyModel(
        private val userAccount: UserAccount?,
        private val authStage: AuthState.Stage
) : BaseEpoxyModel(R.layout.view_profile_item) {

    init {
        id("ProfileItemEpoxyModel", userAccount.toString(), authStage.toString())
    }

    override fun bind(view: View): Unit = with(view) {
        super.bind(view)

        when (authStage) {
            AuthState.Stage.NOT_SIGNED_IN -> {
                showNoUserData(view)
                profileLayout.visibility = View.GONE
                setOnClickListener { }
                showLoginPart(view)
            }
            AuthState.Stage.SIGNED_IN -> {
                showNoUserData(view)
                profileLayout.visibility = View.GONE
                showVerifyPart(view)
                setOnClickListener {
                    showLogout(context)
                }
            }
            AuthState.Stage.VERIFIED -> {
                noUserLayout.visibility = View.GONE
                showUserData(view)
                setOnClickListener {
                    showLogout(context)
                }
            }
        }
    }

    private fun showUserData(view: View) = with(view) {
        profileLayout.visibility = View.VISIBLE

        userAccount?.codeforcesUser?.run {
            Picasso.get().load(avatar).into(ivProfile)
            tvRank.text = buildRank()
            tvHandle.text = buildHandle()
            tvName.text = buildFullName()

            tvRating.text = buildRating(context)
            tvMaxRating.text = buildMaxRating(context)
            tvContribution.text = buildContribution()
        }

        // TODO add chart
        // TODO add redirecting to UserActivity by clicking
    }

    private fun User.buildRating(context: Context) = SpannableString(
            context.getString(
                    R.string.rating_only,
                    rating?.toString() ?: context.getString(R.string.none)
            )
    ).apply {
        rating?.let {
            val startIndex = indexOf(it.toString())
            val color = getColorByUserRank(rank)
            colorSubstring(startIndex, startIndex + it.toString().length, color)
        }
    }

    private fun User.buildMaxRating(context: Context) = SpannableString(
            context.getString(
                    R.string.max_rating_only,
                    maxRating?.toString() ?: context.getString(R.string.none)
            )
    ).apply {
        maxRating?.let {
            val startIndex = indexOf(it.toString())
            val color = getColorByUserRank(rank)
            colorSubstring(startIndex, startIndex + it.toString().length, color)
        }
    }

    private fun showLogout(context: Context) = AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.log_out))
            .setMessage(context.getString(R.string.do_you_want_to_log_out))
            .setCancelable(false)
            .setPositiveButton(context.getString(R.string.stay_logged_in), null)
            .setNegativeButton(context.getString(R.string.log_out)) { _, _ -> store.dispatch(AuthRequests.LogOut) }
            .create()
            .show()

    private fun showNoUserData(view: View) = with(view) {
        ivProfile.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_alien_head))
        tvRank.text = SpannableString(context.getString(R.string.galactic_master)).apply {
            colorSubstring(0, this.length, R.color.colorPrimary)
        }
        tvHandle.text = SpannableString(context.getString(R.string.form_of_life)).apply {
            colorSubstring(0, this.length, R.color.colorPrimary)
        }
        tvName.text = context.getString(R.string.unknown)
        tvLastUpdate.text = context.getString(R.string.never_updated)
    }

    private fun showLoginPart(view: View) = with(view.noUserLayout) {
        visibility = View.VISIBLE
        tvTitle.text = context.getString(R.string.login_to_identify)
        tvSubtitle.text = context.getString(R.string.get_instant_notifications)
        btnAction.text = context.getString(R.string.login_in_42_seconds)

        btnAction.setOnClickListener {
            context.startActivity(Intent(context, SignInActivity::class.java))
        }
    }

    private fun showVerifyPart(view: View) = with(view.noUserLayout) {
        visibility = View.VISIBLE
        tvTitle.text = context.getString(R.string.verify_account)
        tvSubtitle.text = context.getString(R.string.pass_quick_verification)
        btnAction.text = context.getString(R.string.verify_in_42_seconds)

        btnAction.setOnClickListener {
            context.startActivity(Intent(context, VerificationActivity::class.java))
        }
    }
}
