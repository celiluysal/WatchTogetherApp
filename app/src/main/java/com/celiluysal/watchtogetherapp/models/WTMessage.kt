package com.celiluysal.watchtogetherapp.models

import java.io.Serializable

data class WTMessage(
    var messageId: String,
    var text: String,
    var ownerId: String,
    var sendTime: String
): Serializable {
    fun toDict(): HashMap<*,*> {
        return hashMapOf<String, Any?>(
            "messageId" to messageId,
            "text" to text,
            "ownerId" to ownerId,
            "sendTime" to sendTime
        )
    }
}
