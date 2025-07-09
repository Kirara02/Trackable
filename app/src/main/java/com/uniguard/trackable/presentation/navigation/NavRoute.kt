package com.uniguard.trackable.presentation.navigation

sealed class Route(val route: String) {
    data object Splash : Route("splash")
    data object Login : Route("login")
    data object Scanner : Route("scanner")
    data object UHF : Route("uhf")
    data object Reader : Route("uhf/reader")
    data object Find : Route("uhf/find")
    data object ReadWrite : Route("uhf/read-write")
}
