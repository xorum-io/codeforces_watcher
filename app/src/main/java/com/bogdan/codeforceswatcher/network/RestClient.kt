package com.bogdan.codeforceswatcher.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RestClient {

    private val retrofit by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .build()

        val gsonBuilder = GsonBuilder()
            .setLenient()
            .create()

        val gsonConverterFactory = GsonConverterFactory
            .create(gsonBuilder)

        return@lazy Retrofit.Builder()
            .baseUrl("http://www.codeforces.com/api/")
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    private val codeforcesApi by lazy { retrofit.create(CodeforcesApi::class.java) }

    suspend fun getUsers(handle: String) = codeforcesApi.getUsers(handle)

    fun getRating(handle: String) = codeforcesApi.getRating(handle)

    fun getActions(maxCount: Int = 100, lang: String) = codeforcesApi.getActions(maxCount, lang)

    fun getContests() = codeforcesApi.getContests()
}
