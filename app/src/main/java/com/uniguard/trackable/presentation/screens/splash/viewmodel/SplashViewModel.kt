package com.uniguard.trackable.presentation.screens.splash.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniguard.trackable.data.local.datastore.PreferenceManager
import com.uniguard.trackable.domain.usecase.profile.ProfileUseCase
import com.uniguard.trackable.presentation.screens.splash.state.SplashUiState
import com.uniguard.trackable.presentation.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val useCase: ProfileUseCase,
    private val prefs: PreferenceManager
) : ViewModel() {

    private val _state = MutableStateFlow(SplashUiState())
    val state: StateFlow<SplashUiState> = _state.asStateFlow()

    init {
        validateToken()
    }

    private fun validateToken() {
        viewModelScope.launch {
            val token = prefs.getUserToken().firstOrNull()

            if (token.isNullOrBlank()) {
                _state.value = SplashUiState(isLoading = false, isAuthenticated = false)
                return@launch
            }

            useCase(Unit).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value = SplashUiState(isLoading = false, isAuthenticated = true)
                    }

                    is Resource.Error -> {
                        prefs.clearAll()
                        _state.value = SplashUiState(isLoading = false, isAuthenticated = false)
                    }

                    else -> Unit
                }
            }
        }
    }

}