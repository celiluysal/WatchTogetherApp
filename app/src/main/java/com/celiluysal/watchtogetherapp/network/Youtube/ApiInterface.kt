package com.celiluysal.watchtogetherapp.network.Youtube

import com.celiluysal.watchtogetherapp.network.Youtube.models.VideoDetail
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("videos")
    fun getVideoInfo(@Query("part")part: String = "snippet",
    @Query("id")id: String,
    @Query("key")key: String = "AIzaSyBf_s_NjE-HCZwsgU7Zq5FckWcUg-1V8_Y"): Call<VideoDetail>
}