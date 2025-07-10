package com.uniguard.trackable.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("mobile_access")
    val mobileAccess: Boolean?,
    @SerializedName("role")
    val role: UserRoleDto,
)

data class UserRoleDto(
    @SerializedName("role_name")
    val roleName: String
)