package com.bogdan.codeforceswatcher.util

import android.text.Spannable
import android.text.SpannableString
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.bogdan.codeforceswatcher.CwApp

fun SpannableString.colorSubstring(l: Int, r: Int, color: Int) {
    val foregroundColorSpan = ForegroundColorSpan(ContextCompat.getColor(CwApp.app, color))
    this.setSpan(foregroundColorSpan, l, r, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
}

fun String.linked(characterStyles: List<List<CharacterStyle>>): SpannableString {
    val matches = Regex("<link>([\\w\\s']+)</link>")
            .findAll(this)
            .map { it.destructured.toList().first() }

    val textWithoutTags = replace(Regex("<link>([\\w\\s']+)</link>")) {
        it.destructured.toList().first()
    }

    val result = SpannableString(textWithoutTags)

    for ((index, match) in matches.withIndex()) {
        val positionOfLink = textWithoutTags.findAnyOf(listOf(match))?.first
                ?: return SpannableString(this)

        characterStyles.getOrNull(index)?.forEach { characterStyle ->
            result.apply {
                setSpan(characterStyle, positionOfLink, positionOfLink + match.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    return result
}
