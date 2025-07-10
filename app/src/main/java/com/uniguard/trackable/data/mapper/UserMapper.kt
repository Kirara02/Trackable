package com.uniguard.trackable.data.mapper

import com.uniguard.trackable.data.remote.dto.response.UserDto
import com.uniguard.trackable.domain.model.User
import com.uniguard.trackable.domain.model.UserRole

object UserMapper : Mapper<UserDto, User> {
    override fun map(from: UserDto): User {
        return User(
            id = from.id,
            name = from.name,
            email = from.email,
            mobileAccess = from.mobileAccess,
            role = UserRole(from.role.roleName)
        )
    }

}
