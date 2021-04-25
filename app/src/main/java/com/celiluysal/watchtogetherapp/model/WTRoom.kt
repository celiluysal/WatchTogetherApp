package com.celiluysal.watchtogetherapp.model

data class WTRoom(
    var roomId: String,
    var password: String,
    var roomName: String,
    var ownerId: String,
    var content: WTContent,
    var playlist: MutableList<WTVideo>,
    var users: MutableList<WTUser>,
    var message: MutableList<WTMessage>
)
