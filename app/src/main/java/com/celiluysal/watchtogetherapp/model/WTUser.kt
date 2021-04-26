package com.celiluysal.watchtogetherapp.model

data class WTUser(
    var userId : String,
    var avatarId : Int,
    var fullName : String,
    var email : String
) {
    fun toDict(): HashMap<*,*> {
        return hashMapOf<String, Any?>(
            "userId" to userId,
            "avatarId" to avatarId,
            "fullName" to fullName,
            "email" to email
        )
    }
}
