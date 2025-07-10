package com.uniguard.trackable.data.remote.api

import com.uniguard.trackable.data.remote.dto.request.LoginRequest
import com.uniguard.trackable.data.remote.dto.request.LogoutRequest
import com.uniguard.trackable.data.remote.dto.response.LoginResponse
import com.uniguard.trackable.data.remote.response.BaseResponse
import com.uniguard.trackable.domain.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST

interface ApiService {
    @POST("web-api/auth/login")
    suspend fun login(@Body request: LoginRequest): BaseResponse<LoginResponse>

    @HTTP(method = "DELETE", path = "web-api/auth/logout", hasBody = true)
    suspend fun logout(@Body request: LogoutRequest) : BaseResponse<Unit>

    @GET("web-api/account/profile")
    suspend fun profile() : BaseResponse<User>

}