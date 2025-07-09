package com.uniguard.trackable.data.remote.dto.request

data class LoginRequest(
    val email: String,
    val password: String,
    val latitude: Double,
    val longitude: Double
)
