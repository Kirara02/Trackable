package com.uniguard.trackable.presentation.screens.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniguard.trackable.core.session.UserSessionManager
import com.uniguard.trackable.data.remote.dto.request.LogoutRequest
import com.uniguard.trackable.domain.model.User
import com.uniguard.trackable.domain.usecase.logout.LogoutUseCase
import com.uniguard.trackable.presentation.screens.profile.state.ProfileUiState
import com.uniguard.trackable.presentation.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val sessionManager: UserSessionManager,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    val user: StateFlow<User?> = sessionManager.user

    private val _logoutState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val logoutState: StateFlow<ProfileUiState> = _logoutState.asStateFlow()

    fun logout(lat: Double, lng: Double) {
        viewModelScope.launch {
            val currentUser = sessionManager.user.value
            if (currentUser == null) {
                _logoutState.value = ProfileUiState.Failed("No user session")
                return@launch
            }

            _logoutState.value = ProfileUiState.Loading

            val request = LogoutRequest(
                latitude = lat,
                longitude = lng
            )

            logoutUseCase(request).collect { result ->
                _logoutState.value = when (result) {
                    is Resource.Success -> {
                        sessionManager.logout()
                        ProfileUiState.Success
                    }

                    is Resource.Error -> ProfileUiState.Failed(result.message)
                    else -> ProfileUiState.Idle
                }
            }
        }
    }

}
