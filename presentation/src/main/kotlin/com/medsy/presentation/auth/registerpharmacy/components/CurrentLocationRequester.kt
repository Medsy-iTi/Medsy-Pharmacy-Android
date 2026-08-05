package com.medsy.presentation.auth.registerpharmacy.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource

@Composable
internal fun rememberCurrentLocationRequester(
    onLocationFound: (LatLng) -> Unit,
    onPermissionDenied: () -> Unit,
    onLocationUnavailable: () -> Unit,
): () -> Unit {
    val context = LocalContext.current.applicationContext
    val currentOnLocationFound by rememberUpdatedState(onLocationFound)
    val currentOnPermissionDenied by rememberUpdatedState(onPermissionDenied)
    val currentOnLocationUnavailable by rememberUpdatedState(onLocationUnavailable)
    var cancellationTokenSource by remember { mutableStateOf<CancellationTokenSource?>(null) }

    fun requestLocation() {
        cancellationTokenSource?.cancel()
        cancellationTokenSource = findCurrentOrLastLocation(
            context = context,
            onLocationFound = currentOnLocationFound,
            onLocationUnavailable = currentOnLocationUnavailable,
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        val granted = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            requestLocation()
        } else {
            currentOnPermissionDenied()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            cancellationTokenSource?.cancel()
        }
    }

    return {
        if (context.hasLocationPermission()) {
            requestLocation()
        } else {
            permissionLauncher.launch(LOCATION_PERMISSIONS)
        }
    }
}

private fun Context.hasLocationPermission(): Boolean =
    ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

@SuppressLint("MissingPermission")
private fun findCurrentOrLastLocation(
    context: Context,
    onLocationFound: (LatLng) -> Unit,
    onLocationUnavailable: () -> Unit,
): CancellationTokenSource {
    val client = LocationServices.getFusedLocationProviderClient(context)
    val cancellationTokenSource = CancellationTokenSource()

    fun findLastLocation() {
        if (cancellationTokenSource.token.isCancellationRequested) return

        client.lastLocation
            .addOnSuccessListener { location ->
                if (cancellationTokenSource.token.isCancellationRequested) {
                    return@addOnSuccessListener
                }
                if (location != null) {
                    onLocationFound(LatLng(location.latitude, location.longitude))
                } else {
                    onLocationUnavailable()
                }
            }
            .addOnFailureListener {
                if (!cancellationTokenSource.token.isCancellationRequested) {
                    onLocationUnavailable()
                }
            }
    }

    client.getCurrentLocation(
        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
        cancellationTokenSource.token,
    ).addOnSuccessListener { location ->
        if (cancellationTokenSource.token.isCancellationRequested) {
            return@addOnSuccessListener
        }
        if (location != null) {
            onLocationFound(LatLng(location.latitude, location.longitude))
        } else {
            findLastLocation()
        }
    }.addOnFailureListener {
        findLastLocation()
    }

    return cancellationTokenSource
}

private val LOCATION_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
)
