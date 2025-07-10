package com.uniguard.trackable.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.uniguard.trackable.presentation.screens.find.FindScreen
import com.uniguard.trackable.presentation.screens.login.LoginScreen
import com.uniguard.trackable.presentation.screens.profile.ProfileScreen
import com.uniguard.trackable.presentation.screens.rnw.ReadWriteScreen
import com.uniguard.trackable.presentation.screens.scanner.ScannerScreen
import com.uniguard.trackable.presentation.screens.splash.SplashScreen
import com.uniguard.trackable.presentation.screens.uhf.UhfScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues
) {

    NavHost(
        navController = navController,
        startDestination = Route.Splash.route,
        modifier = Modifier.padding(innerPadding)
    ) {

        composable(Route.Splash.route) {
            SplashScreen(
                navigateToLogin = {
                    navController.navigate(Route.Login.route) {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                navigateToMain = {
                    navController.navigate(Route.Scanner.route) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable(Route.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Route.Scanner.route) {
                        popUpTo(Route.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.Profile.route) {
            ProfileScreen(
                onLoggedOut = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.Profile.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.Scanner.route) {
            ScannerScreen()
        }

        navigation(
            route = Route.UHF.route,
            startDestination = Route.Reader.route
        ) {
            composable(Route.Reader.route) {
                UhfScreen(
                    onNavigateToFind = {
                        navController.navigate(Route.Find.route)
                    },
                    onNavigateToRnW = {
                        navController.navigate(Route.ReadWrite.route)
                    }
                )
            }

            composable(Route.Find.route) {
                FindScreen()
            }

            composable(Route.ReadWrite.route) {
                ReadWriteScreen()
            }
        }
    }
}

