package io.xorum.codeforceswatcher.util

import io.xorum.codeforceswatcher.features.auth.models.UserAccount

lateinit var settings: Settings

interface Settings {

    fun writeUserAccount(userAccount: UserAccount?)

    fun readUserAccount(): UserAccount?

    fun readSpinnerSortPosition(): Int

    fun writeSpinnerSortPosition(spinnerSortPosition: Int)

    fun readProblemsIsFavourite(): Boolean

    fun writeProblemsIsFavourite(isFavourite: Boolean)

    fun readContestsFilters(): Set<String>

    fun writeContestsFilters(filters: Set<String>)

    fun writeLastPinnedPostLink(pinnedPostLink: String)

    fun readLastPinnedPostLink(): String
}
