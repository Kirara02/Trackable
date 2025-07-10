package com.uniguard.trackable.data.remote.auth

import android.util.Log
import com.uniguard.trackable.core.session.SessionLogoutHandler
import com.uniguard.trackable.data.local.datastore.PreferenceManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val preferenceManager: PreferenceManager,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val requestUrl = response.request.url.toString()
        val isLoginRequest = requestUrl.contains("/login", ignoreCase = true)

        // Only trigger logout if it's not the login endpoint
        if (response.code == 401 && !isLoginRequest) {
            Log.d("TokenAuthenticator", "authenticate: Token Invalid for $requestUrl")

            runBlocking {
                preferenceManager.clearAll()
                SessionLogoutHandler.triggerLogout()
            }

            // Don't retry, return null to fail the request
            return null
        }

        // Either not 401 or allowed path, don't handle it
        return null
    }
}

