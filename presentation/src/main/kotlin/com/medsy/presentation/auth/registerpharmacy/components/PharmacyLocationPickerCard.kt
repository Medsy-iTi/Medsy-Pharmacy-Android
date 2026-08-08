package com.medsy.presentation.auth.registerpharmacy.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.medsy.presentation.R

@Composable
internal fun PharmacyLocationPickerCard(
    selectedLatitude: Double?,
    selectedLongitude: Double?,
    @StringRes locationErrorRes: Int?,
    onLocationSelected: (latitude: Double, longitude: Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedLocation = remember(selectedLatitude, selectedLongitude) {
        selectedLatitude?.let { latitude ->
            selectedLongitude?.let { longitude -> LatLng(latitude, longitude) }
        }
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            selectedLocation ?: DEFAULT_LOCATION,
            DEFAULT_ZOOM,
        )
    }
    var locationStatusMessageRes by remember { mutableStateOf<Int?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var expandedStartLocation by remember { mutableStateOf(DEFAULT_LOCATION) }

    val requestCurrentLocation = rememberCurrentLocationRequester(
        onLocationFound = { location ->
            locationStatusMessageRes = null
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(location, CURRENT_LOCATION_ZOOM),
            )
        },
        onPermissionDenied = {
            locationStatusMessageRes = R.string.pharmacy_registration_map_permission_denied
        },
        onLocationUnavailable = {
            locationStatusMessageRes = R.string.pharmacy_registration_map_location_unavailable
        },
    )

    LaunchedEffect(selectedLocation) {
        selectedLocation?.let {
            cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(it, 15f))
        }
    }

    LaunchedEffect(Unit) {
        if (selectedLocation != null) return@LaunchedEffect
        requestCurrentLocation()
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
                PinnedLocationMap(
                    cameraPositionState = cameraPositionState,
                    modifier = Modifier.fillMaxSize(),
                )
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

            locationStatusMessageRes?.let { messageRes ->
                LocationStatusMessage(messageRes = messageRes)
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

private val DEFAULT_LOCATION = LatLng(30.0444, 31.2357)
private const val DEFAULT_ZOOM = 13f
private const val CURRENT_LOCATION_ZOOM = 17f
