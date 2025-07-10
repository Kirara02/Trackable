package com.uniguard.trackable.presentation.screens.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniguard.trackable.core.session.UserSessionManager
import com.uniguard.trackable.data.local.datastore.PreferenceManager
import com.uniguard.trackable.data.remote.dto.request.LoginRequest
import com.uniguard.trackable.domain.usecase.login.LoginUseCase
import com.uniguard.trackable.presentation.screens.login.state.LoginUiState
import com.uniguard.trackable.presentation.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val useCase: LoginUseCase,
    private val preferenceManager: PreferenceManager,
    private val sessionManager: UserSessionManager
) : ViewModel() {

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState = _loginState.asStateFlow()

    fun login(email: String, password: String, lat: Double, lng: Double) {
        val request = LoginRequest(
            email = email,
            password = password,
            latitude = lat,
            longitude = lng
        )

        viewModelScope.launch {
            useCase(request).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _loginState.value = LoginUiState(isLoading = true)
                    }

                    is Resource.Success -> {
                        val token = result.data.accessToken
                        preferenceManager.saveUserToken(token)

                        // ✅ set user ke sessionManager
                        sessionManager.setUser(result.data.user)

                        _loginState.value = LoginUiState(data = result.data)
                    }

                    is Resource.Error -> {
                        _loginState.value = LoginUiState(error = result.message)
                    }
                }
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginUiState()
    }

}