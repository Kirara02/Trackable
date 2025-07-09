package com.uniguard.trackable.data.remote.repository

import com.uniguard.trackable.data.remote.api.ApiService
import com.uniguard.trackable.data.remote.dto.request.LoginRequest
import com.uniguard.trackable.data.remote.dto.request.LogoutRequest
import com.uniguard.trackable.data.remote.dto.response.LoginResponse
import com.uniguard.trackable.data.remote.responses.BaseResponse
import com.uniguard.trackable.domain.model.User
import com.uniguard.trackable.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): AuthRepository {

    override suspend fun login(request: LoginRequest): BaseResponse<LoginResponse> {
        return apiService.login(request)
    }

    override suspend fun logout(request: LogoutRequest): BaseResponse<Unit> {
        return apiService.logout(request)
    }

    override suspend fun profile(): BaseResponse<User> {
        return apiService.profile()
    }


}
