package com.example.regalanavidad.sharedScreens

import android.Manifest
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.regalanavidad.viewmodels.mapaOrganizadorVM
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapsScreen(modifier: Modifier, navController: NavController, mapaOrganizadorVM: mapaOrganizadorVM) {
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val cameraPositionState = rememberCameraPositionState()
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var primeraVez by remember { mutableStateOf(false) }
    val searchSitioRecogida by remember { mutableStateOf(mapaOrganizadorVM.searchSitioRecogida) }
    val sitioRecogida by remember { mutableStateOf(mapaOrganizadorVM.sitioRecogida) }
    var start:String
    var end:String

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
            if(searchSitioRecogida.value == true){
                Button(onClick = {
                    if(sitioRecogida.value?.latitudSitio != null && sitioRecogida.value?.longitudSitio != null && currentLocation != null){
                        start = "${currentLocation!!.latitude},${currentLocation!!.longitude}"
                        end = "${sitioRecogida.value!!.latitudSitio},${sitioRecogida.value!!.longitudSitio}"
                        createRoute(start, end)
                    }
                }) {
                    Text(text = "Trazar ruta")
                }
            }
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    compassEnabled = true,
                    zoomControlsEnabled = true,
                    zoomGesturesEnabled = true,
                    myLocationButtonEnabled = true,
                    rotationGesturesEnabled = true,
                    scrollGesturesEnabled = true,
                    scrollGesturesEnabledDuringRotateOrZoom = true,
                ),
                onMapClick = {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(it, cameraPositionState.position.zoom)
                }
            ){
                if (searchSitioRecogida.value == false) {
                    currentLocation?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Posición actual",
                            snippet = "Usted se encuentra aquí"
                        )
                        if (primeraVez){
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 0.5f)
                            primeraVez = false
                        }
                    }
                } else{
                    currentLocation?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Posición actual",
                            snippet = "Usted se encuentra aquí"
                        )
                    }
                    if(sitioRecogida.value?.latitudSitio != null && sitioRecogida.value?.longitudSitio != null){
                        Marker(
                            state = MarkerState(position = LatLng(sitioRecogida.value!!.latitudSitio, sitioRecogida.value!!.longitudSitio)),
                            title = "Sitio de recogida ${sitioRecogida.value!!.nombreSitio}",
                            snippet = sitioRecogida.value!!.direccionSitio
                        )
                    }
                }
            }
        }
    }

    BackHandler {
        navController.popBackStack()
        mapaOrganizadorVM.searchSitioRecogida.value = false
    }
}

fun getRetrofit():Retrofit{
    return Retrofit.Builder()
        .baseUrl("https://api.openrouteservice.org /")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun createRoute(start:String, end:String){
    CoroutineScope(Dispatchers.IO).launch {
        val call = getRetrofit().create(ApiRouteService::class.java).getRoute("5b3ce3597851110001cf6248137fc99131dc495393d861417cf8cbde", start, end)
        if(call.isSuccessful){
            Log.d("Ruta","Llamada exitosa")
        }
    }
}