package com.uniguard.trackable.core.location

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

@Composable
fun rememberLocationState(
    onLocationRetrieved: (AppLocation) -> Unit
): LocationPermissionState {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (permissionGranted) {
            getLastKnownLocation(context)?.let(onLocationRetrieved)
        }
    }

    return LocationPermissionState(
        context = context,
        requestPermissions = {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    )
}

class LocationPermissionState(
    val context: Context,
    val requestPermissions: () -> Unit
) {
    fun checkPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PermissionChecker.PERMISSION_GRANTED || coarse == PermissionChecker.PERMISSION_GRANTED
    }
}
