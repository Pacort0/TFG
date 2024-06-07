package com.example.regalanavidad.sharedScreens

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.regalanavidad.R
import com.example.regalanavidad.apiRouteService.ApiRouteService
import com.example.regalanavidad.apiRouteService.RouteResponse
import com.example.regalanavidad.modelos.SitioRecogida
import com.example.regalanavidad.ui.theme.Blanco
import com.example.regalanavidad.ui.theme.FondoApp
import com.example.regalanavidad.ui.theme.FondoIndvCards
import com.example.regalanavidad.ui.theme.FondoTarjetaInception
import com.example.regalanavidad.viewmodels.MapaVM
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
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private var rutaCargada = mutableStateOf(false)
private var route = mutableListOf<LatLng>()
private var muestraRuta = mutableStateOf(false)
private var calcularAPie = mutableStateOf(true)
private var calcularCoche = mutableStateOf(false)
private var duracionTrayecto = 0
private var listaSitios = mutableStateOf(emptyList<SitioRecogida>())

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapsScreen(navController: NavController, mapaOrganizadorVM: MapaVM) {
    val context = LocalContext.current
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var ubicacionDenegada by remember { mutableStateOf(!locationPermissionState.hasPermission) }
    var hayInternet by remember { mutableStateOf(hayInternet(connectivityManager)) }
    var cargarSitios by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = locationPermissionState.hasPermission, key2 = hayInternet) {
        if (locationPermissionState.hasPermission) {
            ubicacionDenegada = false
            if (hayInternet(connectivityManager)) {
                hayInternet = true
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
                        fusedLocationClient.removeLocationUpdates(this)
                    }
                }
                try {
                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )
                } catch (e: SecurityException) {
                    Toast.makeText(
                        context,
                        "No se puede acceder a la localización del dispositivo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                hayInternet = false
            }
        } else {
            ubicacionDenegada = true
        }
    }

    LaunchedEffect(key1 = cargarSitios) {
        if (hayInternet(connectivityManager)){
            if(currentLocation != null ) {
                    listaSitios.value +=
                        SitioRecogida(
                        "Ubicación actual",
                        currentLocation!!.latitude,
                        currentLocation!!.longitude,
                        "Calle de la piruleta"
                        )
                }
            listaSitios.value = firestore.getListaSitiosYEventosUnicos()
            cargarSitios = false
        } else {
            hayInternet = false
        }
    }

    if (ubicacionDenegada) {
        DeniedLocationScreen(
            onRequestPermissionAgain = {
                locationPermissionState.launchPermissionRequest()
            }
        )
    } else if (!hayInternet) {
        NoInternetScreen(
            onRetry = {
                hayInternet = true
            }
        )
    } else if (isLoading) {
        PantallaCarga("Cargando mapa...")
    } else {
        Mapa(mapaOrganizadorVM, navController, currentLocation)
        cargarSitios = true
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Mapa(
    mapaOrganizadorVM: MapaVM,
    navController: NavController,
    currentLocation: LatLng?
) {
    val cameraPositionState = rememberCameraPositionState()
    val posicionActual by remember { mutableStateOf(currentLocation) }
    var entraMapa by remember { mutableStateOf(true) }
    val searchSitioRecogida by remember { mutableStateOf(mapaOrganizadorVM.searchSitioRecogida) }
    val sitioRecogida by remember { mutableStateOf(mapaOrganizadorVM.sitioRecogida) }
    var rutaLoading by remember { mutableStateOf(false) }
    var start by remember { mutableStateOf("0,0") }
    var end by remember { mutableStateOf("0,0") }
    var mostrarBarraDestino by remember { mutableStateOf(false) }
    var sitioDestino by remember { mutableStateOf("") }
    var sitioPartida by remember { mutableStateOf("Ubicación actual") }
    var barraPartidaHasFocus by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoApp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(searchSitioRecogida.value == true){
                Row (
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .background(Color.Transparent),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = FondoTarjetaInception,
                            disabledContainerColor = FondoTarjetaInception
                        ),
                        onClick = {
                            calcularAPie.value = true
                            calcularCoche.value = false
                            rutaLoading = true
                            muestraRuta.value = true
                            if(muestraRuta.value){
                                muestraRuta.value = false
                                createRoute(start, end)
                            } },
                        modifier = Modifier
                            .border(1.dp, Color.Black, RoundedCornerShape(15))
                    ) {
                        Icon(painterResource(id = R.drawable.apie), "A pie", tint = Color.Black)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        onClick = {
                            calcularAPie.value = false
                            calcularCoche.value = true
                            rutaLoading = true
                            muestraRuta.value = true
                            if(muestraRuta.value){
                                muestraRuta.value = false
                                createRoute(start, end)
                            } },
                        modifier = Modifier
                            .background(Color.LightGray)
                            .border(1.dp, Color.Black, RoundedCornerShape(15))
                    ) {
                        Icon(painterResource(id = R.drawable.coche_icon), "En coche", tint = Color.Black)
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    if(rutaLoading){
                        CircularProgressIndicator(
                            color = FondoIndvCards
                        )
                        Text(
                            text = "Cargando ruta...",
                            modifier = Modifier.padding(top = 8.dp),
                            color = Color.Black
                        )
                    } else if(muestraRuta.value){
                        if(calcularAPie.value){
                            Text(text = "Borrar ruta a pie", Modifier.padding(end = 2.dp), color = Color.Black)
                        } else {
                            Text(text = "Borrar ruta en coche", Modifier.padding(end = 2.dp), color = Color.Black)
                        }
                        IconButton(
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { muestraRuta.value = false }
                        ) {
                            Icon(Icons.Filled.Clear, contentDescription = "Borrar ruta", tint = Color.Black)
                        }
                    }
                }
            }
            GoogleMap(
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxWidth(),
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
                onMapClick = { latLng: LatLng ->
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, cameraPositionState.position.zoom)
                },
                onMapLoaded = {
                    if (searchSitioRecogida.value == true) {
                        if (sitioRecogida.value?.latitudSitio != null && sitioRecogida.value?.longitudSitio != null && posicionActual?.latitude != 37.4219983 && posicionActual?.longitude != -122.084) {
                            start = "${posicionActual!!.longitude},${posicionActual!!.latitude}"
                            end = "${sitioRecogida.value!!.longitudSitio},${sitioRecogida.value!!.latitudSitio}"
                            createRoute(start, end)
                        } else {
                            start = "-5.986495,37.391524"
                            end = "${sitioRecogida.value!!.longitudSitio},${sitioRecogida.value!!.latitudSitio}"
                            createRoute(start, end)
                        }
                        rutaLoading = true
                    }
                }
            ) {
                if (rutaCargada.value && muestraRuta.value && mapaOrganizadorVM.searchSitioRecogida.value == true) {
                    rutaLoading = false
                    Polyline(points = route)
                }
                if (searchSitioRecogida.value == false) {
                    posicionActual?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Posición actual",
                            snippet = "Usted se encuentra aquí"
                        )
                        if (entraMapa) {
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
                            entraMapa = false
                        }
                    }
                } else {
                    posicionActual?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Posición actual",
                            snippet = "Usted se encuentra aquí"
                        )
                    }
                    sitioRecogida.value?.let { sitio ->
                        val sitioLatLng = LatLng(sitio.latitudSitio, sitio.longitudSitio)
                        Marker(
                            state = MarkerState(position = sitioLatLng),
                            title = "Sitio de recogida ${sitio.nombreSitio}",
                            snippet = sitio.direccionSitio
                        )
                        if (entraMapa) {
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(sitioLatLng, 15f)
                            entraMapa = false
                        }
                    }
                }
            }
            if (searchSitioRecogida.value == true && muestraRuta.value && rutaCargada.value) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.weight(0.33f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Column {
                            Text(text = "Salida:", color = Color.Black)
                            Text(text = "Posición actual", color = Color.Black)
                        }
                    }
                    Column(
                        modifier = Modifier.weight(0.33f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column {
                            if (calcularAPie.value) {
                                Text(text = "A pie", color = Color.Black)
                            } else {
                                Text(text = "En coche", color = Color.Black)
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Flecha", Modifier.size(24.dp))
                            Text(text = "$duracionTrayecto minutos", color = Color.Black)
                        }
                    }
                    Column(
                        modifier = Modifier.weight(0.33f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.End
                    ) {
                        Column {
                            Text(text = "Destino:", color = Color.Black)
                            Text(text = "${sitioRecogida.value?.nombreSitio}", color = Color.Black)
                        }
                    }
                }
            }
        }
        Column (
            modifier = Modifier
                .background(Color.Transparent)
                .wrapContentHeight()
                .align(Alignment.TopCenter)
        ) {
            TextField(
                value = sitioPartida,
                onValueChange = { nuevoSitioPartida ->
                    sitioPartida = nuevoSitioPartida
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = FondoIndvCards,
                    unfocusedContainerColor = FondoIndvCards,
                    focusedIndicatorColor = Color.Black,
                    unfocusedIndicatorColor = Color.Black
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Salida",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    ) },
                modifier = Modifier
                    .padding(10.dp)
                    .background(Blanco)
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        barraPartidaHasFocus = focusState.isFocused
                    }
                    .border(1.dp, Color.Black, RoundedCornerShape(15))
            )
            if (barraPartidaHasFocus){
                sitioPartida = ""
            } else {
                sitioPartida = "Ubicación actual"
            }
            if (mostrarBarraDestino){
                TextField(
                    value = sitioDestino,
                    onValueChange = {},
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = FondoIndvCards,
                        unfocusedContainerColor = FondoIndvCards,
                        focusedIndicatorColor = Color.Black,
                        unfocusedIndicatorColor = Color.Black
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "Salida",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        ) },
                    modifier = Modifier
                        .padding(10.dp)
                        .background(Blanco)
                        .fillMaxWidth()
                        .border(1.dp, Color.Black, RoundedCornerShape(15))
                )
            }
        }
        FloatingActionButton(
            onClick = {
                if (!mostrarBarraDestino){
                    mostrarBarraDestino = true
                } },
            containerColor = FondoTarjetaInception,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
                .size(60.dp))
        {
            Icon(painter = painterResource(id = R.drawable.distance), contentDescription = "Cómo llegar")
        }
    }
    BackHandler {
        navController.popBackStack()
        mapaOrganizadorVM.searchSitioRecogida.value = false
    }
}



fun getRetrofit():Retrofit{
    return Retrofit.Builder()
        .baseUrl("https://api.openrouteservice.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun createRoute(start:String, end:String){
    var call: Response<RouteResponse>
    CoroutineScope(Dispatchers.IO).launch {
        call = if (calcularAPie.value){
            getRetrofit().create(ApiRouteService::class.java).getWalkableRoute("5b3ce3597851110001cf6248137fc99131dc495393d861417cf8cbde", start, end)
        } else {
            getRetrofit().create(ApiRouteService::class.java).getDrivingRoute("5b3ce3597851110001cf6248137fc99131dc495393d861417cf8cbde", start, end)
        }
        if(call.isSuccessful){
            route = drawRoute(call.body())
            duracionTrayecto = (getDuration(call.body())/60)
            Log.d("Ruta","Llamada exitosa")
        } else {
            Log.d("Ruta","Llamada fallida")
        }
    }
}

fun drawRoute(routeResponse: RouteResponse?): MutableList<LatLng> {
    val listaCoordenadas = mutableListOf<LatLng>()
    routeResponse?.features?.first()?.geometry?.coordinates?.forEach {
        listaCoordenadas.add(LatLng(it[1], it[0]))
    }
    rutaCargada.value = true
    muestraRuta.value = true
    return listaCoordenadas
}

fun getDuration(routeResponse: RouteResponse?): Int {
    return routeResponse?.features?.first()?.properties?.segments?.first()?.duration?.toInt() ?: 0
}
