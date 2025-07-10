package com.uniguard.trackable.presentation.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uniguard.trackable.presentation.screens.profile.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import com.uniguard.trackable.core.location.rememberLocationState
import com.uniguard.trackable.presentation.screens.profile.state.ProfileUiState

@Composable
fun ProfileScreen(
    onLoggedOut: () -> Unit
) {
    val viewModel: ProfileViewModel = hiltViewModel()

    val context = LocalContext.current
    val user by viewModel.user.collectAsState()
    val logoutState by viewModel.logoutState.collectAsState()
    val scope = rememberCoroutineScope()

    // 📍 Location state
    val locationState = rememberLocationState { location ->
        scope.launch {
            viewModel.logout(location.latitude, location.longitude)
        }
    }

    LaunchedEffect(logoutState) {
        when (logoutState) {
            is ProfileUiState.Success -> onLoggedOut()
            is ProfileUiState.Failed -> {
                val msg = (logoutState as ProfileUiState.Failed).message
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (user != null) {
            Text("👤 ${user?.name}", style = MaterialTheme.typography.headlineSmall)
            Text("📧 ${user?.email}")
            Text("🔐 Role: ${user?.role?.roleName}")
        } else {
            Text("No user data", color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                if (locationState.checkPermission()) {
                    val loc = com.uniguard.trackable.core.location.getLastKnownLocation(locationState.context)
                    if (loc != null) {
                        scope.launch {
                            viewModel.logout(loc.latitude, loc.longitude)
                        }
                    } else {
                        Toast.makeText(context, "Location not available", Toast.LENGTH_SHORT).show()
                        locationState.requestPermissions()
                    }
                } else {
                    locationState.requestPermissions()
                }
            },
            enabled = logoutState != ProfileUiState.Loading
        ) {
            if (logoutState == ProfileUiState.Loading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text("Logout")
            }
        }
    }
}
