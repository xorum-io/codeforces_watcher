package com.bogdan.codeforceswatcher.features.users.redux.requests

import com.bogdan.codeforceswatcher.model.RatingChange
import com.google.gson.annotations.SerializedName

data class RatingChangeResponse(
    val status: String,
    @SerializedName("result") val ratingChanges: List<RatingChange>
)