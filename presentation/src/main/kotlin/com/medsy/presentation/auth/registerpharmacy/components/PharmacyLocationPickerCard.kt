package com.medsy.presentation.auth.registerpharmacy.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.medsy.designsystem.components.MedsyButton
import com.medsy.presentation.R

@Composable
internal fun PharmacyLocationPickerCard(
    selectedLatitude: Double?,
    selectedLongitude: Double?,
    @StringRes locationErrorRes: Int?,
    onLocationSelected: (latitude: Double, longitude: Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val selectedLocation = remember(selectedLatitude, selectedLongitude) {
        selectedLatitude?.let { latitude ->
            selectedLongitude?.let { longitude -> LatLng(latitude, longitude) }
        }
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            selectedLocation ?: LatLng(30.0444, 31.2357),
            13f,
        )
    }
    var permissionDenied by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var expandedStartLocation by remember { mutableStateOf(LatLng(30.0444, 31.2357)) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        val granted = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            permissionDenied = false
            centerOnCurrentOrLastLocation(context) { location ->
                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(location, 15f))
            }
        } else {
            permissionDenied = true
        }
    }

    LaunchedEffect(selectedLocation) {
        selectedLocation?.let {
            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(it, 15f))
        }
    }

    LaunchedEffect(Unit) {
        if (selectedLocation != null) return@LaunchedEffect

        if (context.hasLocationPermission()) {
            centerOnCurrentOrLastLocation(context) { location ->
                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(location, 15f))
            }
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.pharmacy_registration_map_title),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Text(
                        text = stringResource(R.string.pharmacy_registration_map_hint),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
            ) {
                LocationMapBox(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = false),
                        uiSettings = MapUiSettings(
                            myLocationButtonEnabled = false,
                            zoomControlsEnabled = true,
                        ),
                    )
                }
                IconButton(
                    onClick = {
                        expandedStartLocation = cameraPositionState.position.target
                        expanded = true
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Fullscreen,
                        contentDescription = stringResource(
                            R.string.pharmacy_registration_map_expand,
                        ),
                    )
                }
            }

            if (permissionDenied) {
                Text(
                    text = stringResource(R.string.pharmacy_registration_map_permission_denied),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }

            locationErrorRes?.let { errorRes ->
                Text(
                    text = stringResource(errorRes),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.error,
                    ),
                )
            }

            selectedLocation?.let { location ->
                SelectedLocationText(location = location)
            }

            OutlinedButton(
                onClick = {
                    val target = cameraPositionState.position.target
                    onLocationSelected(target.latitude, target.longitude)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
            ) {
                Text(stringResource(R.string.pharmacy_registration_map_use_pin))
            }
        }
    }

    if (expanded) {
        FullscreenLocationPicker(
            startLocation = expandedStartLocation,
            onDismiss = { expanded = false },
            onLocationSelected = { location ->
                onLocationSelected(location.latitude, location.longitude)
                expanded = false
            },
        )
    }
}

@Composable
private fun FullscreenLocationPicker(
    startLocation: LatLng,
    onDismiss: () -> Unit,
    onLocationSelected: (LatLng) -> Unit,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startLocation, 16f)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                LocationMapBox(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = false),
                        uiSettings = MapUiSettings(
                            myLocationButtonEnabled = false,
                            zoomControlsEnabled = true,
                        ),
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SelectedLocationText(location = cameraPositionState.position.target)
                        MedsyButton(
                            onClick = {
                                onLocationSelected(cameraPositionState.position.target)
                            },
                        ) {
                            Text(stringResource(R.string.pharmacy_registration_map_use_pin))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationMapBox(
    modifier: Modifier = Modifier,
    map: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        map()
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.Center)
                .size(40.dp),
        )
    }
}

@Composable
private fun SelectedLocationText(
    location: LatLng,
) {
    Text(
        text = stringResource(
            R.string.pharmacy_registration_location_selected,
            location.latitude,
            location.longitude,
        ),
        style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )
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
private fun centerOnCurrentOrLastLocation(
    context: Context,
    onLocationFound: (LatLng) -> Unit,
) {
    val client = LocationServices.getFusedLocationProviderClient(context)
    val cancellationTokenSource = CancellationTokenSource()

    client.getCurrentLocation(
        Priority.PRIORITY_BALANCED_POWER_ACCURACY,
        cancellationTokenSource.token,
    ).addOnSuccessListener { location ->
        if (location != null) {
            onLocationFound(LatLng(location.latitude, location.longitude))
        } else {
            client.lastLocation.addOnSuccessListener { lastLocation ->
                if (lastLocation != null) {
                    onLocationFound(LatLng(lastLocation.latitude, lastLocation.longitude))
                }
            }
        }
    }.addOnFailureListener {
        client.lastLocation.addOnSuccessListener { lastLocation ->
            if (lastLocation != null) {
                onLocationFound(LatLng(lastLocation.latitude, lastLocation.longitude))
            }
        }
    }
}
