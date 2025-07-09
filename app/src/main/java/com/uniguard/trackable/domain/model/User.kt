package com.uniguard.trackable.domain.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("mobile_access")
    val mobileAccess: Boolean?,

    @SerializedName("role")
    val role: UserRole,
)

data class UserRole(
    @SerializedName("role_name")
    val roleName: String
)