package com.uniguard.trackable.presentation.screens.profile.state

sealed class ProfileUiState {
    data object Idle : ProfileUiState()
    data object Loading : ProfileUiState()
    data object Success : ProfileUiState()
    data class Failed(val message: String) : ProfileUiState()
}