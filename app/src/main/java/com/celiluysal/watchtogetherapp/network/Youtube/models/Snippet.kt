package com.celiluysal.watchtogetherapp.network.Youtube.models

data class Snippet(
    val channelTitle: String,
    val publishedAt: String,
    val thumbnails: Thumbnails,
    val title: String
)