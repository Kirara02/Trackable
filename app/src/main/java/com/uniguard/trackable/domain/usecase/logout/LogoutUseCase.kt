package com.uniguard.trackable.domain.usecase.logout

import com.google.gson.Gson
import com.uniguard.trackable.data.remote.dto.request.LogoutRequest
import com.uniguard.trackable.domain.repository.AuthRepository
import com.uniguard.trackable.domain.usecase.FlowUseCase
import com.uniguard.trackable.presentation.state.Resource
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository,
    gson: Gson
) : FlowUseCase<LogoutRequest, String>(gson){
    override suspend fun execute(params: LogoutRequest): Resource<String> {
        val response = repository.logout(params)
        return if (response.success) {
            Resource.Success(response.message)
        } else {
            Resource.Error(message = response.message, code = response.error?.code)
        }
    }
}