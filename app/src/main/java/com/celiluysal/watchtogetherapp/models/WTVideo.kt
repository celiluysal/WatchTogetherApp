package com.celiluysal.watchtogetherapp.models

import java.io.Serializable

data class WTVideo(
    var videoId: String,
    var title: String,
    var thumbnail: String,
    var channel: String,
    var sendTime: String
): Serializable {
    fun toDict(): HashMap<*,*>{
        return hashMapOf<String, Any?>(
            "videoId" to videoId,
            "title" to title,
            "thumbnail" to thumbnail,
            "channel" to channel,
            "sendTime" to sendTime
        )
    }
}