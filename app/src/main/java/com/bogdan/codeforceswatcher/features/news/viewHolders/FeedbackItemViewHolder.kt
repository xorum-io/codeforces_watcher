package com.bogdan.codeforceswatcher.features.news.viewHolders

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.view_feedback_card_view.view.*

class FeedbackItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvTitle: TextView = view.tvTitle
    val btnPositive: Button = view.btnPositive
    val btnNegative: Button = view.btnNegative

    var onCrossClickListener: (() -> Unit)? = null
    var onNegativeBtnClickListener: (() -> Unit)? = null
    var onPositiveBtnClickListener: (() -> Unit)? = null

    init {
        view.run {
            ivCrossFeedback.setOnClickListener {
                onCrossClickListener?.invoke()
            }

            btnNegative.setOnClickListener {
                onNegativeBtnClickListener?.invoke()
            }

            btnPositive.setOnClickListener {
                onPositiveBtnClickListener?.invoke()
            }
        }
    }
}
