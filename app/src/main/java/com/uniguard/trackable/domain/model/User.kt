package com.uniguard.trackable.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val mobileAccess: Boolean?,
    val role: UserRole,
)

data class UserRole(
    val roleName: String
)
