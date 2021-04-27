package com.celiluysal.watchtogetherapp.models

import java.io.Serializable

data class WTContent(
    var video: WTVideo,
    var currentTime: Int,
    var isPlaying: Boolean
): Serializable {
    fun toDict(): HashMap<*,*> {
        return hashMapOf<String, Any?>(
            "video" to video.toDict(),
            "currentTime" to currentTime,
            "isPlaying" to isPlaying
        )
    }
}
