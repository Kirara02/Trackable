package com.uniguard.trackable.data.remote.repository

import com.uniguard.trackable.data.mapper.LoginMapper
import com.uniguard.trackable.data.mapper.UserMapper
import com.uniguard.trackable.data.remote.api.ApiService
import com.uniguard.trackable.data.remote.dto.request.LoginRequest
import com.uniguard.trackable.data.remote.dto.request.LogoutRequest
import com.uniguard.trackable.data.remote.response.BaseResponse
import com.uniguard.trackable.domain.model.LoginResult
import com.uniguard.trackable.domain.model.User
import com.uniguard.trackable.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AuthRepository {

    override suspend fun login(request: LoginRequest): BaseResponse<LoginResult> {
        val response = apiService.login(request)
        val mapped = response.data?.let { LoginMapper.map(it) }

        return BaseResponse(
            success = response.success,
            message = response.message,
            error = response.error,
            data = mapped,
            meta = response.meta
        )
    }

    override suspend fun logout(request: LogoutRequest): BaseResponse<Unit> {
        return apiService.logout(request)
    }

    override suspend fun profile(): BaseResponse<User> {
        val response = apiService.profile()
        val mapped = response.data?.let { UserMapper.map(it) }

        return BaseResponse(
            success = response.success,
            message = response.message,
            error = response.error,
            data = mapped,
            meta = response.meta
        )
    }
}

