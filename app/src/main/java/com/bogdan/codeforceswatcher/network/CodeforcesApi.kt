package com.bogdan.codeforceswatcher.network

import com.bogdan.codeforceswatcher.features.actions.redux.requests.ActionsResponse
import com.bogdan.codeforceswatcher.features.contests.redux.requests.ContestsResponse
import com.bogdan.codeforceswatcher.features.users.redux.requests.RatingChangeResponse
import com.bogdan.codeforceswatcher.features.users.redux.requests.UsersResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CodeforcesApi {

    @GET("user.rating")
    fun getRating(@Query("handle") handle: String): Call<RatingChangeResponse>

    @GET("user.info")
    suspend fun getUsers(@Query("handles") handles: String): Response<UsersResponse>

    @GET("contest.list")
    fun getContests(): Call<ContestsResponse>

    @GET("recentActions")
    fun getActions(
        @Query("maxCount") maxCount: Int,
        @Query("lang") lang: String
    ): Call<ActionsResponse>
}