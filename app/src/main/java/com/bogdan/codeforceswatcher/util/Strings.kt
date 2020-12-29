package com.bogdan.codeforceswatcher.util

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.bogdan.codeforceswatcher.CwApp
import com.bogdan.codeforceswatcher.R

fun SpannableString.colorSubstring(l: Int, r: Int, color: Int) {
    val foregroundColorSpan = ForegroundColorSpan(ContextCompat.getColor(CwApp.app, color))
    this.setSpan(foregroundColorSpan, l, r, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun String.colorLinkTagPart(): SpannableString {
    val match = Regex("<link>(.+)<\\/link>").find(this)?.destructured?.toList()?.first()
            ?: return SpannableString(this)

    val textWithoutTags = replace(Regex("<link>(.+)<\\/link>"), match)

    val positionOfLink = textWithoutTags.findAnyOf(listOf(match))?.first
            ?: return SpannableString(this)

    return SpannableString(textWithoutTags).apply {
        colorSubstring(positionOfLink, positionOfLink + match.length, R.color.colorPrimary)
    }
}