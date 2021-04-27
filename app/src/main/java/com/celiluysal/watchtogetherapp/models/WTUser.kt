package com.celiluysal.watchtogetherapp.models

import java.io.Serializable

data class WTUser(
    var userId : String,
    var avatarId : Int,
    var fullName : String,
    var email : String
): Serializable {
    fun toDict(): HashMap<*,*> {
        return hashMapOf<String, Any?>(
            "userId" to userId,
            "avatarId" to avatarId,
            "fullName" to fullName,
            "email" to email
        )
    }
}
