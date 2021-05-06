package com.celiluysal.watchtogetherapp.network.Firebase

data class RegisterRequestModel(
    var email: String,
    var password: String,
    var fullName: String,
    var avatarId: Int
)
