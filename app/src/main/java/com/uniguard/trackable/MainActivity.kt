package com.uniguard.trackable

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.uniguard.trackable.core.session.SessionLogoutHandler
import com.uniguard.trackable.core.session.UserSessionManager
import com.uniguard.trackable.presentation.navigation.AppNavHost
import com.uniguard.trackable.presentation.navigation.BottomNav
import com.uniguard.trackable.presentation.navigation.Route
import com.uniguard.trackable.presentation.navigation.model.BottomBarScreen
import com.uniguard.trackable.presentation.theme.TrackableTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            TrackableTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val isLoggedOut by SessionLogoutHandler.isLoggedOut.collectAsState()

                LaunchedEffect(isLoggedOut) {
                    if (isLoggedOut) {
                        navController.navigate(Route.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                        SessionLogoutHandler.reset()
                    }
                }

                val navigationItemContentList = listOf(
                    BottomBarScreen.Scan,
                    BottomBarScreen.Uhf,
                    BottomBarScreen.Profile
                )

                val shouldShowBottomBar = currentDestination?.route in listOf(
                    Route.Scanner.route,
                    Route.UHF.route,
                    Route.Reader.route,
                    Route.Profile.route
                )

                Scaffold(
                    bottomBar = {
                        AnimatedVisibility(
                            visible = shouldShowBottomBar,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            BottomNav(
                                navController = navController,
                                currentDestination = currentDestination,
                                navigationItemContentList = navigationItemContentList
                            )
                        }
                    }
                ) {
                    AppNavHost(
                        navController = navController,
                        innerPadding = it
                    )
                }
            }
        }
    }
}
