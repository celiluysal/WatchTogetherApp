package com.celiluysal.watchtogetherapp.models

import java.io.Serializable

data class WTRoom(
    var roomId: String,
    var password: String?,
    var roomName: String,
    var ownerId: String,
    var content: WTContent?,
    var playlist: MutableList<WTVideo>?,
    var users: MutableList<String>,
    var messages: MutableList<WTMessage>?
): Serializable {
    fun toDict(): HashMap<*, *> {
        return hashMapOf<String, Any?>(
            "roomId" to roomId,
            "password" to password,
            "roomName" to roomName,
            "ownerId" to ownerId,
            "content" to content,
            "playlist" to playlist,
            "users" to users,
            "message" to messages
        )
    }
}
