package com.uniguard.trackable.presentation.screens.splash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniguard.trackable.core.session.UserSessionManager
import com.uniguard.trackable.presentation.screens.splash.state.SplashUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userSessionManager: UserSessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(SplashUiState())
    val state: StateFlow<SplashUiState> = _state.asStateFlow()

    init {
        validateSession()
    }

    private fun validateSession() {
        viewModelScope.launch {
            val isAuthenticated = userSessionManager.loadSession()
            _state.value = SplashUiState(
                isLoading = false,
                isAuthenticated = isAuthenticated
            )
        }
    }
}
