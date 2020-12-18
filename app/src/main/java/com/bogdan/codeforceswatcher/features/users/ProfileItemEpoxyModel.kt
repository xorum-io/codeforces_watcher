package com.bogdan.codeforceswatcher.features.users

import android.view.View
import androidx.core.content.ContextCompat
import com.bogdan.codeforceswatcher.R
import com.bogdan.codeforceswatcher.epoxy.BaseEpoxyModel
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.xorum.codeforceswatcher.util.avatar
import kotlinx.android.synthetic.main.view_user_item.view.*

class ProfileItemEpoxyModel : BaseEpoxyModel(R.layout.view_profile_item) {

    init {
        id("ProfileItemEpoxyModel")
    }

    override fun bind(view: View): Unit = with(view) {
        super.bind(view)

    }
}
