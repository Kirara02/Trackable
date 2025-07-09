package com.uniguard.trackable.data.remote.api

import com.uniguard.trackable.data.remote.dto.request.LoginRequest
import com.uniguard.trackable.data.remote.dto.request.LogoutRequest
import com.uniguard.trackable.data.remote.dto.response.LoginResponse
import com.uniguard.trackable.data.remote.responses.BaseResponse
import com.uniguard.trackable.domain.model.User
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("web-api/auth/login")
    suspend fun login(@Body request: LoginRequest): BaseResponse<LoginResponse>

    @DELETE("web-api/auth/logout")
    suspend fun logout(@Body request: LogoutRequest) : BaseResponse<Unit>

    @GET("web-api/account/profile")
    suspend fun profile() : BaseResponse<User>

}