package com.uniguard.trackable.data.remote.dto.response

import com.google.gson.annotations.SerializedName
import com.uniguard.trackable.domain.model.User

data class LoginResponse(
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("token_type")
    val tokenType: String,

    @SerializedName("user")
    val user: User,
)