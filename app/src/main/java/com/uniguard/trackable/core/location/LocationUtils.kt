package com.uniguard.trackable.core.location

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

data class AppLocation(
    val latitude: Double,
    val longitude: Double
)

@Suppress("MissingPermission")
fun getLastKnownLocation(context: Context): AppLocation? {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PermissionChecker.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PermissionChecker.PERMISSION_GRANTED
    ) return null

    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val providers = locationManager.getProviders(true)

    val bestLocation: Location? = providers.mapNotNull { provider ->
        locationManager.getLastKnownLocation(provider)
    }.maxByOrNull { it.accuracy }

    return bestLocation?.let {
        AppLocation(it.latitude, it.longitude)
    }
}
