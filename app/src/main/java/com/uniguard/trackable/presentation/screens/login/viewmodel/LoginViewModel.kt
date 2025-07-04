package com.uniguard.trackable.presentation.screens.login.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : ViewModel() {

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState

    fun login(username: String, password: String) {
        _loginState.value = LoginState(true, username)
    }

    fun logout() {
        _loginState.value = LoginState()
    }
}

data class LoginState(
    val isLoggedIn: Boolean = false,
    val username: String = ""
)
