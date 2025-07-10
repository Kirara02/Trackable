package com.uniguard.trackable.domain.model

data class LoginResult(
    val accessToken: String,
    val tokenType: String,
    val user: User
)