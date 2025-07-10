package com.uniguard.trackable.domain.usecase.login

import com.google.gson.Gson
import com.uniguard.trackable.data.remote.dto.request.LoginRequest
import com.uniguard.trackable.domain.model.LoginResult
import com.uniguard.trackable.domain.repository.AuthRepository
import com.uniguard.trackable.domain.usecase.FlowUseCase
import com.uniguard.trackable.presentation.state.Resource
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository,
    gson: Gson
) : FlowUseCase<LoginRequest, LoginResult>(gson) {

    override suspend fun execute(params: LoginRequest): Resource<LoginResult> {
        val response = repository.login(params)
        return if (response.success && response.data != null) {
            Resource.Success(response.data)
        } else {
            Resource.Error(message = response.message, code = response.error?.code)
        }
    }
}
