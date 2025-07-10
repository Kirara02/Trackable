package com.uniguard.trackable.presentation.screens.login.state

import com.uniguard.trackable.domain.model.LoginResult

data class LoginUiState(
    val isLoading: Boolean = false,
    val data: LoginResult? = null,
    val error: String? = null
)