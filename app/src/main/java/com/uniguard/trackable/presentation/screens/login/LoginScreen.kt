package com.uniguard.trackable.presentation.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uniguard.trackable.presentation.screens.login.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    val loginState by loginViewModel.loginState.collectAsState()

    LaunchedEffect(loginState.isLoggedIn) {
        if (loginState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text("🔐 Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") }
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { loginViewModel.login(username, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
    }
}
