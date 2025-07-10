package com.uniguard.trackable.domain.repository

import com.uniguard.trackable.data.remote.dto.request.LoginRequest
import com.uniguard.trackable.data.remote.dto.request.LogoutRequest
import com.uniguard.trackable.data.remote.response.BaseResponse
import com.uniguard.trackable.domain.model.LoginResult
import com.uniguard.trackable.domain.model.User

interface AuthRepository {
    suspend fun login(request: LoginRequest): BaseResponse<LoginResult>
    suspend fun logout(request: LogoutRequest): BaseResponse<Unit>
    suspend fun profile(): BaseResponse<User>
}