package io.xorum.codeforceswatcher.features.users.models

import kotlinx.serialization.Serializable

@Serializable
data class RatingChange(
        // val contestId: Int,
        val contestName: String,
        // val handle: String,
        val rank: Int,
        val ratingUpdateTimeSeconds: Long,
        val oldRating: Int,
        val newRating: Int
) {
    fun toChartItem() = ChartItem(
            newRating.toString(),
            (newRating - oldRating).toString(),
            rank.toString(),
            contestName
    )
}
