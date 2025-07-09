package com.uniguard.trackable.presentation.screens.login.state

import com.uniguard.trackable.data.remote.dto.response.LoginResponse

data class LoginUiState(
    val isLoading: Boolean = false,
    val data: LoginResponse? = null,
    val error: String? = null
)