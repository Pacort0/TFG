import android.Manifest
import android.location.Geocoder
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapsScreen(modifier: Modifier, navController: NavController) {
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val cameraPositionState = rememberCameraPositionState()
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var searched by remember { mutableStateOf(false) }
    val markerState = remember { mutableStateOf<MarkerState?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var searchedLocation by remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(Unit) {
        if (locationPermissionState.hasPermission) {
            val locationRequest = LocationRequest.Builder(100, 300)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY).build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.let {
                        for (location in it.locations) {
                            currentLocation = LatLng(location.latitude, location.longitude)
                            isLoading = false
                        }
                    }
                }
            }

            try {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper()).await()
            } catch (e: SecurityException) {
                Toast.makeText(context, "No se puede acceder a la localización del dispositivo", Toast.LENGTH_SHORT).show()
            }
        } else {
            locationPermissionState.launchPermissionRequest()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
            Text(
                text = "Cargando tu posición actual...",
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar sitio") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    val geocoder = Geocoder(context)
                    val addresses = geocoder.getFromLocationName(searchQuery, 1)
                    if (addresses != null) {
                        if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            searchedLocation = LatLng(address.latitude, address.longitude)
                            searchedLocation?.let {
                                markerState.value = MarkerState(position = it)
                                cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 10f)
                            }
                            searched = true
                        }
                    }
                }),
                modifier = Modifier.fillMaxWidth()
            )
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    compassEnabled = true,
                    zoomControlsEnabled = true,
                    zoomGesturesEnabled = true,
                    myLocationButtonEnabled = true,
                    rotationGesturesEnabled = true,
                    scrollGesturesEnabled = true
                )
            ) {
                if (!searched || searchQuery.isEmpty()) {
                    currentLocation?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Posición actual",
                            snippet = "Usted se encuentra aquí"
                        )
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 10f)
                    }
                } else {
                    markerState.value?.let { markerState ->
                        Marker(
                            state = markerState,
                            title = "Posición buscada",
                            snippet = "Resultado de la búsqueda"
                        )
                    }
                }
            }
        }
    }

    BackHandler {
        if (searched) {
            searched = false
            cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation!!, 10f)
        } else {
            navController.popBackStack()
        }
    }
}