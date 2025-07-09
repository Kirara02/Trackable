package com.uniguard.trackable.domain.usecase.profile

import com.google.gson.Gson
import com.uniguard.trackable.domain.model.User
import com.uniguard.trackable.domain.repository.AuthRepository
import com.uniguard.trackable.domain.usecase.FlowUseCase
import com.uniguard.trackable.presentation.state.Resource
import javax.inject.Inject

class ProfileUseCase @Inject constructor(
    private val repository: AuthRepository,
    gson: Gson
) : FlowUseCase<Unit, User>(gson) {
    override suspend fun execute(params: Unit): Resource<User> {
        val response = repository.profile()
        return if (response.success && response.data != null) {
            Resource.Success(response.data)
        } else {
            Resource.Error(message = response.message, code = response.error?.code)
        }
    }
}