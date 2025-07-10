package com.uniguard.trackable.data.mapper

import com.uniguard.trackable.data.remote.dto.response.LoginResponse
import com.uniguard.trackable.domain.model.LoginResult

object LoginMapper : Mapper<LoginResponse, LoginResult> {
    override fun map(from: LoginResponse): LoginResult {
        return LoginResult(
            accessToken = from.accessToken,
            tokenType = from.tokenType,
            user = UserMapper.map(from.user)
        )
    }
}