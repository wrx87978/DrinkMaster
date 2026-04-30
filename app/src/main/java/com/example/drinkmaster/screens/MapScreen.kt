package com.example.drinkmaster.screens

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen() {
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(Unit) {
        locationPermissionState.launchPermissionRequest()
    }

    // Nowa lokalizacja: Pawilony Nowy Świat (prawdziwe zagłębie barów)
    val recommendedBarLocation = LatLng(52.2325, 21.0205)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(recommendedBarLocation, 16f)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Gdzie na drinka?",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = locationPermissionState.status.isGranted,
                    // Dodatkowo: pokazuje natężenie ruchu (opcjonalnie)
                    isTrafficEnabled = true
                ),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = locationPermissionState.status.isGranted,
                    zoomControlsEnabled = true
                )
            ) {
                Marker(
                    state = MarkerState(position = recommendedBarLocation),
                    title = "Pawilony Nowy Świat",
                    snippet = "Zagłębie najlepszych barów w mieście!"
                )
            }

            // Podpowiedź, co zrobić, gdy nie widać niebieskiej kropki
            if (!locationPermissionState.status.isGranted) {
                Text(
                    text = "Kliknij 'Pozwól', aby zobaczyć swoją pozycję",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 50.dp)
                )
            }
        }
    }
}
