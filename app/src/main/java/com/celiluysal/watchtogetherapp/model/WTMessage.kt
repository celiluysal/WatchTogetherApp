package com.celiluysal.watchtogetherapp.model

data class WTMessage(
    var messageId: String,
    var text: String,
    var ownerId: String,
    var sendTime: String
)
