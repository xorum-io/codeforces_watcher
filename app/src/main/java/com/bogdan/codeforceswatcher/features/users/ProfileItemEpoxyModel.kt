package com.bogdan.codeforceswatcher.features.users

import android.content.Intent
import android.text.SpannableString
import android.view.View
import androidx.core.content.ContextCompat
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.epoxy.BaseEpoxyModel
import com.bogdan.codeforceswatcher.features.auth.SignInActivity
import com.bogdan.codeforceswatcher.features.auth.VerificationActivity
import com.bogdan.codeforceswatcher.util.colorSubstring
import com.squareup.picasso.Picasso
import io.xorum.codeforceswatcher.features.auth.UserAccount
import kotlinx.android.synthetic.main.no_user_card_layout.view.*
import kotlinx.android.synthetic.main.view_profile_item.view.*

class ProfileItemEpoxyModel(private val userAccount: UserAccount?) : BaseEpoxyModel(R.layout.view_profile_item) {

    init {
        id("ProfileItemEpoxyModel", userAccount.toString())
    }

    override fun bind(view: View): Unit = with(view) {
        super.bind(view)

        when {
            userAccount == null -> {
                showNoUserData(view)
                profileLayout.visibility = View.GONE
                showLoginPart(view)
            }
            userAccount.codeforcesUser == null -> {
                showNoUserData(view)
                profileLayout.visibility = View.GONE
                showVerifyPart(view)
            }
            else -> {
                noUserLayout.visibility = View.GONE
                showUserData(view)
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
        }

        // TODO add remain UI components
    }

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
