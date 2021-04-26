package com.celiluysal.watchtogetherapp.model

data class WTContent(
    var video: WTVideo,
    var currentTime: Int,
    var isPlaying: Boolean
) {
    fun toDict(): HashMap<*,*> {
        return hashMapOf<String, Any?>(
            "video" to video.toDict(),
            "currentTime" to currentTime,
            "isPlaying" to isPlaying
        )
    }
}
