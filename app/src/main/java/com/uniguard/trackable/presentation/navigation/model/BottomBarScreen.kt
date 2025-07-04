package com.uniguard.trackable.presentation.navigation.model

import com.uniguard.trackable.R
import com.uniguard.trackable.presentation.navigation.Route

sealed class BottomBarScreen (val route: String) {
    data object Scan: BottomBar(
        route = Route.Scanner.route,
        icon = R.drawable.outline_barcode_scanner,
        iconFocused = R.drawable.outline_barcode_scanner,
        titleResId = R.string.scan
    )

    data object  Uhf: BottomBar(
        route = Route.UHF.route,
        icon = R.drawable.outline_network_wifi,
        iconFocused = R.drawable.outline_network_wifi,
        titleResId = R.string.uhf
    )
}