package com.celiluysal.watchtogetherapp.model

data class WTVideo(
    var videoId: String,
    var title: String,
    var thumbnail: String,
    var channel: String,
    var duration: Int
) {
    fun toDict(): HashMap<*,*>{
        return hashMapOf<String, Any?>(
            "videoId" to videoId,
            "title" to title,
            "thumbnail" to thumbnail,
            "channel" to channel,
            "duration" to duration
        )
    }
}