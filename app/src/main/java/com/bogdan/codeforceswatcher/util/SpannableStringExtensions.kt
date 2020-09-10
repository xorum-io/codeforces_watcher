package com.bogdan.codeforceswatcher.util

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.bogdan.codeforceswatcher.CwApp

fun SpannableString.colorSubstring(l: Int, r: Int, color: Int) {
    val foregroundColorSpan = ForegroundColorSpan(ContextCompat.getColor(CwApp.app, color))
    this.setSpan(foregroundColorSpan, l, r, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}