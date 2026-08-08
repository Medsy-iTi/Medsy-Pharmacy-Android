package com.medsy.presentation.auth.registerpharmacy.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import com.medsy.designsystem.components.MedsyButton
import com.medsy.presentation.R

@Composable
internal fun FullscreenLocationPicker(
    startLocation: LatLng,
    onDismiss: () -> Unit,
    onLocationSelected: (LatLng) -> Unit,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startLocation, FULLSCREEN_ZOOM)
    }
    var currentLocationMessageRes by remember { mutableStateOf<Int?>(null) }
    val requestCurrentLocation = rememberCurrentLocationRequester(
        onLocationFound = { location ->
            currentLocationMessageRes = null
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(location, CURRENT_LOCATION_ZOOM),
            )
        },
        onPermissionDenied = {
            currentLocationMessageRes =
                R.string.pharmacy_registration_map_current_location_permission_required
        },
        onLocationUnavailable = {
            currentLocationMessageRes = R.string.pharmacy_registration_map_location_unavailable
        },
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                PinnedLocationMap(
                    cameraPositionState = cameraPositionState,
                    modifier = Modifier.fillMaxSize(),
                )

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
                        currentLocationMessageRes?.let { messageRes ->
                            LocationStatusMessage(messageRes = messageRes)
                        }
                        OutlinedButton(
                            onClick = requestCurrentLocation,
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MyLocation,
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                stringResource(
                                    R.string.pharmacy_registration_map_use_current_location,
                                ),
                            )
                        }
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

private const val FULLSCREEN_ZOOM = 16f
private const val CURRENT_LOCATION_ZOOM = 17f
