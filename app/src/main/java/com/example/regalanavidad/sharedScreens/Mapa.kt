package com.example.regalanavidad.sharedScreens

import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.regalanavidad.R
import com.example.regalanavidad.apiRouteService.ApiRouteService
import com.example.regalanavidad.modelos.RouteResponse
import com.example.regalanavidad.dal.getRetrofit
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

private var route = mutableListOf<LatLng>()
private var muestraRuta = mutableStateOf(false)
private var calcularAPie = mutableStateOf(true)
private var calcularCoche = mutableStateOf(false)
private var calcularBici = mutableStateOf(false)
private var duracionTrayectoAPie = mutableIntStateOf(0)
private var duracionTrayectoBici = mutableIntStateOf(0)
private var duracionTrayectoCoche = mutableIntStateOf(0)
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
    var ubicacionDenegada by remember { mutableStateOf(false) }
    var hayInternet by remember { mutableStateOf(hayInternet(connectivityManager)) }
    var cargarSitios by remember { mutableStateOf(false) }
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var ubicacionActivada by remember{ mutableStateOf(false) }

    LaunchedEffect(key1 = locationPermissionState.hasPermission, key2 = hayInternet, key3 = ubicacionActivada) {
        if (locationPermissionState.hasPermission) {
            ubicacionDenegada = false
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                ubicacionActivada = true
                if (hayInternet) {
                    hayInternet = true
                    val locationRequest = LocationRequest.Builder(100, 300)
                        .setPriority(Priority.PRIORITY_HIGH_ACCURACY).build()

                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            locationResult.let {
                                for (location in it.locations) {
                                    currentLocation = LatLng(location.latitude, location.longitude)
                                    isLoading = false
                                    cargarSitios =
                                        true // Activar carga de sitios una vez obtenida la ubicación
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
            }
        } else {
            ubicacionDenegada = true
        }
    }

    LaunchedEffect(key1 = cargarSitios) {
        if (hayInternet) {
            listaSitios.value = firestore.getListaSitiosYEventosUnicos()
            cargarSitios = false
        }
    }

    if (isLoading) {
        PantallaCarga("Cargando mapa...")
    } else if (ubicacionDenegada) {
        DeniedLocationScreen(
            onRequestPermissionAgain = {
                locationPermissionState.launchPermissionRequest()
            }
        )
    } else if (!ubicacionActivada) {
        NoUbicacionScreen (
            onRetry = {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    ubicacionActivada = true
                }
            }
        )
    } else if (!hayInternet) {
        NoInternetScreen(
            onRetry = {
                hayInternet = hayInternet(connectivityManager)
            }
        )
    } else {
        Mapa(mapaOrganizadorVM, navController, currentLocation, connectivityManager)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Mapa(
    mapaOrganizadorVM: MapaVM,
    navController: NavController,
    currentLocation: LatLng?,
    connectivityManager: ConnectivityManager
) {
    val cameraPositionState = rememberCameraPositionState()
    val posicionActual by remember { mutableStateOf(currentLocation) }
    var mueveCamara by remember { mutableStateOf(true) }
    var searchSitioRecogida by remember { mutableStateOf(mapaOrganizadorVM.searchSitioRecogida.value) }
    val sitioRecogida by remember { mutableStateOf(mapaOrganizadorVM.sitioRecogida.value) }
    var rutaLoading by remember { mutableStateOf(false) }
    var start by remember { mutableStateOf("0,0") }
    var end by remember { mutableStateOf("0,0") }
    var mostrarBarraDestino by remember { mutableStateOf(false) }
    var sitioDestino by remember { mutableStateOf(SitioRecogida()) }
    var sitioPartida by remember { mutableStateOf(SitioRecogida()) }
    var mostrarListaSitios by remember { mutableStateOf(false) }
    var focusBarraPartida by remember {mutableStateOf(false)}
    var focusBarraDestino by remember { mutableStateOf(false) }
    var nombreSitioPartida by remember { mutableStateOf("") }
    var nombreSitioDestino by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var listaFiltrada by remember { mutableStateOf(listaSitios.value) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoApp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                }
            ) {
                if (muestraRuta.value) {
                    rutaLoading = false
                    Polyline(points = route)
                }

                if (searchSitioRecogida == false && start == "0,0" && end == "0,0") {
                    posicionActual?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Posición actual",
                            snippet = "Usted se encuentra aquí"
                        )
                        if (mueveCamara) {
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
                            mueveCamara = false
                        }
                    }
                } else if (muestraRuta.value || searchSitioRecogida == true) {
                    val startLatLng: LatLng
                    val endLatLng: LatLng

                    if (searchSitioRecogida == true) {
                        mostrarBarraDestino = true
                        nombreSitioPartida = "Ubicación actual"
                        nombreSitioDestino = sitioRecogida?.nombreSitio ?: ""

                        if (posicionActual?.latitude != 37.4219983 && posicionActual?.longitude != -122.084) {
                            start = "${posicionActual!!.longitude},${posicionActual!!.latitude}"
                            end = "${sitioRecogida!!.longitudSitio},${sitioRecogida!!.latitudSitio}"
                        } else {
                            start = "-5.986495,37.391524"
                            end = "${sitioRecogida!!.longitudSitio},${sitioRecogida!!.latitudSitio}"
                        }
                        createRoute(start, end, connectivityManager)

                        startLatLng = LatLng(posicionActual!!.latitude, posicionActual!!.longitude)
                        endLatLng = LatLng(sitioRecogida!!.latitudSitio, sitioRecogida!!.longitudSitio)
                    } else {
                        startLatLng = LatLng(start.split(",")[1].toDouble(), start.split(",")[0].toDouble())
                        endLatLng = LatLng(end.split(",")[1].toDouble(), end.split(",")[0].toDouble())
                    }

                    val midLat = (startLatLng.latitude + endLatLng.latitude) / 2
                    val midLng = (startLatLng.longitude + endLatLng.longitude) / 2
                    val midLatLng = LatLng(midLat, midLng)

                    Marker(
                        state = MarkerState(position = startLatLng),
                        title = nombreSitioPartida,
                        snippet = "Punto de partida"
                    )
                    Marker(
                        state = MarkerState(position = endLatLng),
                        title = nombreSitioDestino,
                        snippet = "Punto de destino"
                    )
                    if (mueveCamara){
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(midLatLng, 13f)
                        mueveCamara = false
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
                value = nombreSitioPartida,
                onValueChange = { nuevoSitioPartida ->
                    nombreSitioPartida = nuevoSitioPartida
                    listaFiltrada = filtrarSitios(nuevoSitioPartida, listaSitios.value, sitioDestino)
                },
                placeholder = { Text(text = if (mostrarBarraDestino) {"Lugar de partida"} else "Buscar un sitio", color = Color.DarkGray)},
                textStyle = TextStyle(color = Color.Black),
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
                        if (focusState.isFocused) {
                            nombreSitioPartida = ""
                            mostrarListaSitios = true
                            focusBarraPartida = true
                        } else {
                            focusBarraPartida = false
                            mostrarListaSitios = false
                        }
                    }
                    .border(1.dp, Color.Black, RoundedCornerShape(15))
            )
            if (mostrarBarraDestino){
                TextField(
                    value = nombreSitioDestino,
                    onValueChange = { nuevoSitioDestino ->
                        nombreSitioDestino = nuevoSitioDestino
                        listaFiltrada = filtrarSitios(nuevoSitioDestino, listaSitios.value, sitioPartida)},
                    placeholder = { Text(text = "Lugar de destino", color = Color.DarkGray)},
                    textStyle = TextStyle(color = Color.Black),
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
                            if (focusState.isFocused) {
                                nombreSitioDestino = ""
                                mostrarListaSitios = true
                                focusBarraPartida = false
                                focusBarraDestino = true
                            } else {
                                focusBarraDestino = false
                                mostrarListaSitios = false
                            }
                        }
                        .border(1.dp, Color.Black, RoundedCornerShape(15))
                )
            }
            if (muestraRuta.value) {
                Row (
                    modifier = Modifier
                        .background(Color.Transparent)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    ElevatedButton(
                        modifier = Modifier.wrapContentSize(),
                        onClick = {
                            muestraRuta.value = false
                            calcularAPie.value = true
                            calcularBici.value = false
                            calcularCoche.value = false
                            createRoute(start, end, connectivityManager)
                        },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = FondoTarjetaInception,
                            disabledContainerColor = FondoIndvCards
                        ),
                        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painterResource(id = R.drawable.apie),
                                contentDescription = "A pie",
                                Modifier.size(24.dp),
                                tint = if (calcularAPie.value) Color.Black else Color.DarkGray,
                            )
                            if (calcularAPie.value && duracionTrayectoAPie.intValue != 0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "${duracionTrayectoAPie.intValue} mins", fontSize = 15.sp, color = Color.Black)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    ElevatedButton(
                        modifier = Modifier.wrapContentSize(),
                        onClick = {
                            muestraRuta.value = false
                            calcularAPie.value = false
                            calcularBici.value = true
                            calcularCoche.value = false
                            createRoute(start, end, connectivityManager)
                        },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = FondoTarjetaInception,
                            disabledContainerColor = FondoIndvCards
                        ),
                        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp))
                    {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painterResource(id = R.drawable.cycling),
                                contentDescription = "En bici",
                                Modifier.size(24.dp),
                                tint = if (calcularBici.value) Color.Black else Color.DarkGray,
                            )
                            if (calcularBici.value && duracionTrayectoBici.intValue != 0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(text = "${duracionTrayectoBici.intValue} mins", fontSize = 15.sp, color = Color.Black)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    ElevatedButton(
                        modifier = Modifier.wrapContentSize(),
                        onClick = {
                            muestraRuta.value = false
                            calcularAPie.value = false
                            calcularBici.value = false
                            calcularCoche.value = true
                            createRoute(start, end, connectivityManager)
                        },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = FondoTarjetaInception,
                            disabledContainerColor = FondoIndvCards
                        ),
                        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp))
                    {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painterResource(id = R.drawable.coche_icon),
                                contentDescription = "En coche",
                                Modifier.size(24.dp),
                                tint = if (calcularCoche.value) Color.Black else Color.DarkGray,
                            )
                            if (calcularCoche.value && duracionTrayectoCoche.intValue != 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "${duracionTrayectoCoche.intValue} mins", fontSize = 15.sp, color = Color.Black)
                            }
                        }
                    }
                }
            }
            if (mostrarListaSitios) {
                if (listaFiltrada.isNotEmpty()) {
                    LazyColumn (
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(8.dp)
                            .background(FondoApp)
                    ) {
                        items(listaFiltrada.size) { index ->
                            Card (
                                modifier = Modifier
                                    .height(55.dp)
                                    .background(Color.Transparent)
                                    .border(1.dp, Color.Gray, RoundedCornerShape(20))
                                    .clip(RoundedCornerShape(20)),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.Transparent
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                        .background(Color.Transparent)
                                        .clickable {
                                            if (focusBarraPartida) {
                                                sitioPartida = listaFiltrada[index]
                                                if (!mostrarBarraDestino) {
                                                    start =
                                                        currentLocation!!.longitude.toString() + "," + currentLocation.latitude.toString()
                                                    end =
                                                        sitioPartida.longitudSitio.toString() + "," + sitioPartida.latitudSitio.toString()
                                                    mostrarListaSitios = false
                                                    mostrarBarraDestino = true
                                                    nombreSitioDestino = sitioPartida.nombreSitio
                                                    nombreSitioPartida = "Ubicación actual"
                                                    createRoute(start, end, connectivityManager)
                                                    mueveCamara = true
                                                    searchSitioRecogida = false
                                                    rutaLoading = true
                                                } else {
                                                    start =
                                                        sitioPartida.longitudSitio.toString() + "," + sitioPartida.latitudSitio.toString()
                                                    nombreSitioPartida = sitioPartida.nombreSitio
                                                }
                                                mostrarListaSitios = false
                                            } else {
                                                sitioDestino = listaFiltrada[index]
                                                nombreSitioDestino = sitioDestino.nombreSitio
                                                end =
                                                    sitioDestino.longitudSitio.toString() + "," + sitioDestino.latitudSitio.toString()
                                                mostrarListaSitios = false
                                            }
                                            if (nombreSitioDestino != "") {
                                                if (nombreSitioPartida == "") {
                                                    nombreSitioPartida = "Ubicación actual"
                                                    if (currentLocation != null) {
                                                        start =
                                                            currentLocation.longitude.toString() + "," + currentLocation.latitude.toString()
                                                    }
                                                }
                                                mostrarListaSitios = false
                                                createRoute(start, end, connectivityManager)
                                                mueveCamara = true
                                                searchSitioRecogida = false
                                                rutaLoading = true
                                            }
                                            focusManager.clearFocus()
                                        },
                                ) {
                                    Text(
                                        text = listaFiltrada[index].nombreSitio,
                                        color = Color.Black,
                                        fontSize = 15.sp,
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = {
                if (!mostrarBarraDestino){
                    mostrarBarraDestino = true
                    focusManager.clearFocus()
                } },
            containerColor = FondoTarjetaInception,
            modifier = Modifier
                .alpha(muestraRuta.value.let { if (it) 0.0f else 1.0f })
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 16.dp)
                .size(60.dp))
        {
            Icon(painter = painterResource(id = R.drawable.distance), contentDescription = "Cómo llegar", tint = Color.Black)
        }
    }
    BackHandler {
        if (muestraRuta.value){
            if (searchSitioRecogida == true) {
                searchSitioRecogida = false
            }
            muestraRuta.value = false
            start = "0,0"
            end = "0,0"
        } else if (focusBarraDestino){
            focusBarraDestino = false
            mostrarListaSitios = false
            nombreSitioDestino = ""
        } else  if (focusBarraPartida) {
            focusBarraPartida = false
            mostrarListaSitios = false
            nombreSitioPartida = ""
        } else if (mostrarBarraDestino){
            mostrarBarraDestino = false
        } else if(nombreSitioPartida != ""){
            nombreSitioPartida = ""
        } else {
            navController.popBackStack()
        }
        focusManager.clearFocus()
    }
}

// Define la función fuera del composable
fun filtrarSitios(query: String, sitios: List<SitioRecogida>, sitioExcluido: SitioRecogida?): List<SitioRecogida> {
    return sitios.filter {
        it.nombreSitio.contains(query, ignoreCase = true) && it != sitioExcluido
    }
}

fun createRoute(start:String, end:String, connectivityManager: ConnectivityManager){
    var call: Response<RouteResponse>
    if (hayInternet(connectivityManager)) {
        CoroutineScope(Dispatchers.IO).launch {
            call = if (calcularAPie.value){
                getRetrofit().create(ApiRouteService::class.java).getWalkableRoute("5b3ce3597851110001cf6248137fc99131dc495393d861417cf8cbde", start, end)
            } else if (calcularBici.value) {
                getRetrofit().create(ApiRouteService::class.java).getCyclingRoute("5b3ce3597851110001cf6248137fc99131dc495393d861417cf8cbde", start, end)
            } else {
                getRetrofit().create(ApiRouteService::class.java).getDrivingRoute("5b3ce3597851110001cf6248137fc99131dc495393d861417cf8cbde", start, end)
            }
            if(call.isSuccessful){
                route = drawRoute(call.body())
                if (calcularAPie.value){
                    duracionTrayectoAPie.intValue = (getDuration(call.body())/60)
                } else if (calcularBici.value) {
                    duracionTrayectoBici.intValue = (getDuration(call.body())/60)
                } else {
                    duracionTrayectoCoche.intValue = (getDuration(call.body())/60)
                }
            } else {
                Log.d("Ruta","Llamada fallida")
            }
        }
    }
}

fun drawRoute(routeResponse: RouteResponse?): MutableList<LatLng> {
    val listaCoordenadas = mutableListOf<LatLng>()
    routeResponse?.features?.first()?.geometry?.coordinates?.forEach {
        listaCoordenadas.add(LatLng(it[1], it[0]))
    }
    muestraRuta.value = true
    return listaCoordenadas
}

fun getDuration(routeResponse: RouteResponse?): Int {
    return routeResponse?.features?.first()?.properties?.segments?.first()?.duration?.toInt() ?: 0
}
