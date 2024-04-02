package com.example.regalanavidad

import android.annotation.SuppressLint
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.regalanavidad.modelos.Usuario
import com.example.regalanavidad.organizadorScreens.OrganizadorHomeScreen
import com.example.regalanavidad.voluntarioScreens.VoluntarioHomeScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

data class TabBarItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeAmount: Int? = null
)

val drawerItems = listOf("Información", "Contáctanos", "Patrocinadores", "Otros años")
val auth = Firebase.auth
var usuario = Usuario()
val firestore = FirestoreManager()

class Home : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        val correo = intent.getStringExtra("correo")

        runBlocking {
            val task = launch {
                usuario = correo?.let { firestore.findUserByEmail(it) }!!
            }
            task.join()
        }

        val esVoluntario = usuario.nombreRango == "Voluntario"

        super.onCreate(savedInstanceState)

        setContent {
            if(esVoluntario){
                VoluntarioHomeScreen()
            } else {
                OrganizadorHomeScreen()
            }
        }
    }
}

@Composable
fun TabView(tabBarItems: List<TabBarItem>, navController: NavController, onTabSelected: (String) -> Unit) {
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    NavigationBar {
        // looping over each tab to generate the views and navigation for each item
        tabBarItems.forEachIndexed { index, tabBarItem ->
            NavigationBarItem(
                selected = selectedTabIndex == index,
                onClick = {
                    selectedTabIndex = index
                    onTabSelected(tabBarItem.title) // Invoke the callback with the selected tab's title
                    navController.navigate(tabBarItem.title)
                },
                icon = {
                    TabBarIconView(
                        isSelected = selectedTabIndex == index,
                        selectedIcon = tabBarItem.selectedIcon,
                        unselectedIcon = tabBarItem.unselectedIcon,
                        title = tabBarItem.title,
                        badgeAmount = tabBarItem.badgeAmount
                    )
                },
                label = {Text(tabBarItem.title)})
        }
    }
}

// This component helps to clean up the API call from our TabView above,
// but could just as easily be added inside the TabView without creating this custom component
@Composable
fun TabBarIconView(
    isSelected: Boolean,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    title: String,
    badgeAmount: Int? = null
) {
    BadgedBox(badge = { TabBarBadgeView(badgeAmount) }) {
        Icon(
            imageVector = if (isSelected) {selectedIcon} else {unselectedIcon},
            contentDescription = title
        )
    }
}

// This component helps to clean up the API call from our TabBarIconView above,
// but could just as easily be added inside the TabBarIconView without creating this custom component
@Composable
fun TabBarBadgeView(count: Int? = null) {
    if (count != null) {
        Badge {
            Text(count.toString())
        }
    }
}

@Composable
fun ScreenContent(modifier: Modifier = Modifier, screenTitle: String, navController: NavController) {
    when (screenTitle){
        "Home" -> HomeScreen(modifier)
        "Alerts" -> AlertsScreen(modifier = modifier)
        "Mapa" -> MapsScreen(modifier = modifier, navController)
        "More" -> MoreTabsScreen(modifier = modifier)
    }
}

@Composable
fun HomeScreen(modifier: Modifier){
    auth.currentUser?.reload() // Recargamos el usuario para comprobar cualquier actualización
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            text = "Hola ${usuario.nombre}!",
            modifier = modifier.padding(0.dp,10.dp)
        )
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(0.dp, 5.dp)) {
                Card(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(0.dp, 0.dp, 5.dp, 0.dp)) {
                    Column {
                        Text(text = "Dinero recaudado: 1€")
                    }
                }
                Card(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(5.dp, 0.dp, 0.dp, 0.dp)) {
                    Column {
                        Text(text = "Sitios en los que recogemos: ")
                    }
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(5.dp, 0.dp)) {
                Card(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(0.dp, 0.dp, 5.dp, 0.dp)) {
                    Column {
                        Text(text = "Fechas y eventos")
                    }
                }
                Card(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(5.dp, 0.dp, 0.dp, 0.dp)) {
                    Column {
                        Text(text = "Redes sociales: ")
                    }
                }
            }
        }
    }
}
@Composable
fun AlertsScreen(modifier: Modifier){
    Text(
        text = "Hello ${usuario.uid}!",
        modifier = modifier
    )
}


/*@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapsScreen(modifier: Modifier, navController: NavController) {
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val locationPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val cameraPositionState = rememberCameraPositionState()
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var searched by remember { mutableStateOf(false) }
    val markerState = remember { mutableStateOf<MarkerState?>(null) }
    var isLoading by remember { mutableStateOf(true) } // New loading state
    var searchedLocation by remember { mutableStateOf<LatLng?>(null) }
    var shouldUpdateCamera by remember { mutableStateOf(true) }


    DisposableEffect(Unit) {
        onDispose {
            shouldUpdateCamera = false
        }
    }
    LaunchedEffect(Unit) {
        if (locationPermissionState.hasPermission) {
            val locationRequest = LocationRequest.create().apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
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
}*/


@Composable
fun MoreTabsScreen(modifier: Modifier){
    Text(
        text = "Hello ${usuario.nombreRango}!",
        modifier = modifier
    )
}

/* Si hay tiempo retomamos esta idea
@Composable
fun SelectProfilePictureScreen(images: List<ImageBitmap>, onImageSelected: (Int) -> Unit) {
    LazyColumn {
        itemsIndexed(images) { index, image ->
            Image(
                bitmap = image,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .height(100.dp)
                    .clickable { onImageSelected(index) }
            )
        }
    }
}*/
