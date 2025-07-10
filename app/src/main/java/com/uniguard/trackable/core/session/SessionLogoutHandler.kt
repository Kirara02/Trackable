package com.uniguard.trackable.core.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object SessionLogoutHandler {
    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut: StateFlow<Boolean> = _isLoggedOut

    fun triggerLogout() {
        _isLoggedOut.value = true
    }

    fun reset() {
        _isLoggedOut.value = false
    }
}