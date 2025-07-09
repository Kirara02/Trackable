package com.uniguard.trackable.presentation.screens.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uniguard.trackable.core.location.getLastKnownLocation
import com.uniguard.trackable.core.location.rememberLocationState
import com.uniguard.trackable.presentation.screens.login.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    val loginState by loginViewModel.loginState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var pendingLogin by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val locationState = rememberLocationState { location ->
        if(pendingLogin) {
            loginViewModel.login(
                email = email,
                password = password,
                lat = location.latitude,
                lng = location.longitude
            )
            pendingLogin = false
        }
    }

    LaunchedEffect(loginState.data) {
        if(loginState.data != null) {
            onLoginSuccess()
        }
    }

    LaunchedEffect(loginState.error) {
        loginState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text("🔐 Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        if (loginState.error != null) {
            Text(
                text = loginState.error ?: "Unknown error",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if(locationState.checkPermission()){
                    val location = getLastKnownLocation(locationState.context)
                    if(location != null) {
                        loginViewModel.login(
                            email = email,
                            password = password,
                            lat = location.latitude,
                            lng = location.longitude
                        )
                    } else {
                        Toast.makeText(context, "📍 Failed to get location. Please try again.", Toast.LENGTH_SHORT).show()
                        pendingLogin = true
                    }
                } else {
                    pendingLogin = true
                    locationState.requestPermissions()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loginState.isLoading
        ) {
            if (loginState.isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text("Login")
            }
        }
    }
}
