package com.uniguard.trackable.presentation.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.uniguard.trackable.R
import com.uniguard.trackable.presentation.screens.splash.viewmodel.SplashViewModel

@Composable
fun SplashScreen(
    navigateToLogin: () -> Unit,
    navigateToMain: () -> Unit
) {
    val viewModel: SplashViewModel = hiltViewModel()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isAuthenticated) {
        when (state.isAuthenticated) {
            true -> navigateToMain()
            false -> navigateToLogin()
            null -> Unit
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier.wrapContentSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.timly),
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp)
            )
        }
    }
}
