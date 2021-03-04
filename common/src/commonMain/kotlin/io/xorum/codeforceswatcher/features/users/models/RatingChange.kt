package io.xorum.codeforceswatcher.features.users.models

import kotlinx.serialization.Serializable

@Serializable
data class RatingChange(
        val contestName: String,
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
