package com.celiluysal.watchtogetherapp.Firebase

data class RegisterRequestModel(
    var email: String,
    var password: String,
    var fullName: String,
    var avatarId: Int
)
