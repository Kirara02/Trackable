package com.uniguard.trackable.data.remote.response

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: T?,

    @SerializedName("error")
    val error: ErrorResponse?,

    @SerializedName("meta")
    val meta: MetaResponse?
)

data class ErrorResponse(
    @SerializedName("code")
    val code: Int,

    @SerializedName("details")
    val details: String
)

data class MetaResponse(
    @SerializedName("timestamp")
    val timestamp: String,

    @SerializedName("total")
    val total: Int?,

    @SerializedName("page")
    val page: Int?,

    @SerializedName("limit")
    val limit: Int?,

    @SerializedName("total_pages")
    val totalPages: Int?
)
