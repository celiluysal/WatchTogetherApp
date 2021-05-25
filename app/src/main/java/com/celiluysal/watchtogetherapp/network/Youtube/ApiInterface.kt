package com.celiluysal.watchtogetherapp.network.Youtube

import com.celiluysal.watchtogetherapp.BuildConfig
import com.celiluysal.watchtogetherapp.network.Youtube.models.VideoDetail
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("videos")
    fun getVideoInfo(@Query("part")part: String = "snippet",
    @Query("id")id: String,
    @Query("key")key: String = BuildConfig.YOUTUBE_API_KEY ): Call<VideoDetail>


}