package com.kodonho.aroc

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationHelper(private val context: Context) {
    companion object {
        const val REQUEST_LOCATION_PERMISSION = 100
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val permissionsLocationUpApi29Impl = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    @TargetApi(Build.VERSION_CODES.P)
    private val permissionsLocationDownApi29Impl = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    fun requestLocation() {
        if (Build.VERSION.SDK_INT >= 29) {
            if (!permissionsGranted(permissionsLocationUpApi29Impl)) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    permissionsLocationUpApi29Impl,
                    REQUEST_LOCATION_PERMISSION
                )
            }
        } else {
            if (!permissionsGranted(permissionsLocationDownApi29Impl)) {
                ActivityCompat.requestPermissions(
                    context as Activity,
                    permissionsLocationDownApi29Impl,
                    REQUEST_LOCATION_PERMISSION
                )
            }
        }
    }

    private fun permissionsGranted(permissions: Array<String>): Boolean {
        return permissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
    }

    fun isLocationPermitted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 29) {
            permissionsGranted(permissionsLocationUpApi29Impl)
        } else {
            permissionsGranted(permissionsLocationDownApi29Impl)
        }
    }
}
