package com.uniguard.trackable.core.session

import com.uniguard.trackable.data.local.datastore.PreferenceManager
import com.uniguard.trackable.domain.model.User
import com.uniguard.trackable.domain.usecase.profile.ProfileUseCase
import com.uniguard.trackable.presentation.state.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSessionManager @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val profileUseCase: ProfileUseCase
) {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    suspend fun loadSession(): Boolean {
        val token = preferenceManager.getUserToken().firstOrNull()

        if (token.isNullOrBlank()) {
            _user.value = null
            return false
        }

        var isValid = false

        try {
            profileUseCase(Unit).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _user.value = result.data
                        isValid = true
                    }
                    is Resource.Error -> {
                        preferenceManager.clearAll()
                        _user.value = null
                        isValid = false
                    }
                    else -> Unit
                }
            }
        } catch (e: Exception) {
            preferenceManager.clearAll()
            _user.value = null
            isValid = false
        }

        return isValid
    }

    fun setUser(user: User) {
        _user.value = user
    }

    suspend fun logout() {
        _user.value = null
        preferenceManager.clearAll()
        SessionLogoutHandler.triggerLogout()
    }
}
