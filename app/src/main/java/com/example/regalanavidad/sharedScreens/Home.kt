package com.example.regalanavidad.sharedScreens

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.BitmapFactory.Options
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.example.regalanavidad.BuildConfig.MAPS_API_KEY
import com.example.regalanavidad.modelos.SitioRecogida
import com.example.regalanavidad.modelos.Usuario
import com.example.regalanavidad.organizadorScreens.OrganizadorHomeScreen
import com.example.regalanavidad.viewmodels.mapaOrganizadorVM
import com.example.regalanavidad.voluntarioScreens.VoluntarioHomeScreen
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

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
val sitiosRecogidaConfirmados = mutableListOf<SitioRecogida>()
private lateinit var placesClient: PlacesClient

class Home : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        val correo = intent.getStringExtra("correo")
        Places.initialize(this, MAPS_API_KEY)
        placesClient = Places.createClient(this)

        runBlocking {
            val task = launch {
                usuario = correo?.let { firestore.findUserByEmail(it) }!!
            }
            task.join()
        }

        val esVoluntario = usuario.nombreRango == "Voluntario"
        val mapaOrganizadorVM = mapaOrganizadorVM()

        super.onCreate(savedInstanceState)

        setContent {
            var estadoMapa by remember { mutableStateOf(false) }

            if(esVoluntario){
                VoluntarioHomeScreen(estadoMapa, mapaOrganizadorVM){
                        estado -> estadoMapa = estado
                }
            } else {
                OrganizadorHomeScreen(estadoMapa, mapaOrganizadorVM){
                        estado -> estadoMapa = estado
                }
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

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ScreenContent(modifier: Modifier = Modifier, screenTitle: String, navController: NavController, onMapaCambiado: (Boolean) -> Unit, mapaOrganizadorVM: mapaOrganizadorVM) {
    when (screenTitle){
        "Home" -> {
            HomeScreen(modifier, navController, mapaOrganizadorVM)
            onMapaCambiado(false)
        }
        "Alerts" -> {
            AlertsScreen(modifier)
            onMapaCambiado(false)
        }
        "Mail" -> {
            MailScreen()
            onMapaCambiado(false)
        }
        "Mapa" -> {
            MapsScreen(modifier, navController, mapaOrganizadorVM)
            onMapaCambiado(true)
        }
        "More" -> {
            MoreTabsScreen(modifier)
            onMapaCambiado(false)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HomeScreen(modifier: Modifier, navController: NavController, mapaOrganizadorVM: mapaOrganizadorVM){
    auth.currentUser?.reload() // Recargamos el usuario para comprobar cualquier actualización

    val context = LocalContext.current
    var agregaSitio by remember { mutableStateOf(false) }
    var muestraListaSitios by remember { mutableStateOf(false) }
    var textoBusqueda by remember { mutableStateOf("") }
    val firestore = FirestoreManager()
    val scope = CoroutineScope(Dispatchers.Main)
    var haySitios by remember { mutableStateOf(false) }
    var recargarDatos by remember { mutableStateOf(true) }
    var sitiosLoading by remember { mutableStateOf(true) }
    val canEditSitios = checkIfCanEditSitios(usuario.nombreRango)
    var navegaSitio by remember { mutableStateOf(false) }

    if (muestraListaSitios) {
        Dialog(onDismissRequest = { muestraListaSitios = false }) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(35.dp)
                .clip(RoundedCornerShape(20.dp))) {
                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .background(color = Color.White)) {
                }
                if (haySitios && !sitiosLoading){
                    ListaSitiosConfirmados(
                        sitiosRecogidaConfirmados,
                        false,
                        canEditSitios,
                        onElementoEliminado = {elementoEliminado -> recargarDatos = elementoEliminado},
                        onSitioEscogido = { sitioRecogida -> mapaOrganizadorVM.sitioRecogida.value = sitioRecogida
                            navegaSitio = true
                        }
                    )
                } else {
                    Text(text = "No hay sitios de recogida confirmados")
                }
                FloatingActionButton(
                    onClick = {
                        agregaSitio = true
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(0.dp, 0.dp, 14.dp, 14.dp)) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar sitio")
                }
            }
        }
        if (agregaSitio) {
            var prediccionesNuevoSitioRecogida by remember { mutableStateOf<List<SitioRecogida>>(mutableListOf()) }

            Dialog(onDismissRequest = { agregaSitio = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(305.dp)
                        .background(Color.LightGray)
                        .padding(35.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    Column {
                        OutlinedTextField(
                            value = textoBusqueda,
                            onValueChange = { nuevaBusqueda ->
                                textoBusqueda = nuevaBusqueda
                                scope.launch {
                                    prediccionesNuevoSitioRecogida = obtenerPredicciones(nuevaBusqueda)
                                }
                            },
                            label = { Text("Buscar") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        LazyColumn {
                            items(prediccionesNuevoSitioRecogida.size) { index ->
                                Card(
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .fillMaxWidth()
                                        .clickable {
                                            scope.launch(Dispatchers.Main) {
                                                firestore.insertaSitioRecogida(
                                                    prediccionesNuevoSitioRecogida[index]
                                                )
                                                textoBusqueda = ""
                                                recargarDatos = true
                                                agregaSitio = false
                                            }
                                        }
                                        .padding(0.dp, 5.dp)
                                ) {
                                    LazyRow {
                                        if(index < prediccionesNuevoSitioRecogida.size - 1){
                                            item{Text(text = prediccionesNuevoSitioRecogida[index].nombreSitio + " - " + prediccionesNuevoSitioRecogida[index].direccionSitio)}
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

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
                    .padding(0.dp, 0.dp, 5.dp, 0.dp)
                    .let {
                        if (usuario.nombreRango == "Coordinador" || usuario.nombreRango == "Tesorería") {
                            it.clickable {
                                Toast
                                    .makeText(context, "Clickado", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else it
                    }) {
                    Column {
                        Text(text = "Dinero recaudado: 1€")
                    }
                }
                Card(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(5.dp, 0.dp, 0.dp, 0.dp)
                    .let {
                        if (canEditSitios) {
                            it.clickable {
                                Toast
                                    .makeText(context, "Clickado", Toast.LENGTH_SHORT)
                                    .show()
                                muestraListaSitios = true
                            }
                        } else it
                    }) {
                    Column {
                        Text(text = "Sitios en los que recogemos: ")
                        LaunchedEffect(key1 = recargarDatos){
                            sitiosLoading = true
                            sitiosRecogidaConfirmados.clear()
                            val sitiosRecogida = firestore.getSitiosRecogida()
                            sitiosRecogida.forEach { sitioRecogida ->
                                sitiosRecogidaConfirmados.add(sitioRecogida)
                            }
                            haySitios = sitiosRecogidaConfirmados.size != 0
                            recargarDatos = false
                            sitiosLoading = false
                        }
                        if (sitiosLoading) {
                            Text(text = "Cargando...")
                        } else {
                            if (haySitios){
                                ListaSitiosConfirmados(
                                    sitiosRecogidaConfirmados,
                                    true,
                                    canEditSitios,
                                    onElementoEliminado = {elementoEliminado -> recargarDatos = elementoEliminado},
                                    onSitioEscogido = { sitioRecogida ->
                                        mapaOrganizadorVM.sitioRecogida.value = sitioRecogida
                                    }
                                )
                            } else {
                                Text(text = "No hay sitios de recogida confirmados")
                            }
                        }
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
                    .padding(0.dp, 0.dp, 5.dp, 0.dp)
                    .let {
                        if (usuario.nombreRango == "Coordinador" || usuario.nombreRango == "RR.II.") {
                            it.clickable {
                                Toast
                                    .makeText(context, "Clickado", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else it
                    }) {
                    Column {
                        Text(text = "Fechas y eventos")
                    }
                }
                Card(modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(5.dp, 0.dp, 0.dp, 0.dp)
                    .let {
                        if (usuario.nombreRango == "Coordinador" || usuario.nombreRango == "Imagen") {
                            it.clickable {
                                Toast
                                    .makeText(context, "Clickado", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else it
                    }) {
                    Column {
                        Text(text = "Redes sociales: ")
                    }
                }
            }
        }
    }
    if(navegaSitio){
        mapaOrganizadorVM.searchSitioRecogida.value = true
        agregaSitio = false
        muestraListaSitios = false

        MapsScreen(modifier, navController, mapaOrganizadorVM)
        navController.navigate("Mapa")
        navegaSitio = false
    }
}
@Composable
fun AlertsScreen(modifier: Modifier){
    Text(
        text = "Hello ${usuario.uid}!",
        modifier = modifier
    )
}

@Composable
fun MoreTabsScreen(modifier: Modifier){
    Text(
        text = "Hello ${usuario.nombreRango}!",
        modifier = modifier
    )
}

@Composable
fun MailScreen(){
    var nombreContacto by remember { mutableStateOf("") }
    var correoContacto by remember { mutableStateOf("") }
    var asuntoCorreo by remember { mutableStateOf("") }
    var mensajeCorreo by remember { mutableStateOf("") }
    val contexto = LocalContext.current

    Column (
        modifier = Modifier.padding(10.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            label = { Text(text = "Nombre") },
            value = nombreContacto,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            onValueChange = { nombreContacto = it }
        )
        TextField(
            label = { Text(text = "Correo") },
            value = correoContacto,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            onValueChange = { correoContacto = it }
        )
        TextField(
            label = { Text(text = "Asunto") },
            value = asuntoCorreo,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            onValueChange = { asuntoCorreo = it }
        )
        TextField(
            label = { Text(text = "Mensaje") },
            value = mensajeCorreo,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            onValueChange = { mensajeCorreo = it }
        )
        Button(onClick = {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "message/rfc822"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(correoContacto))
            intent.putExtra(Intent.EXTRA_SUBJECT, asuntoCorreo)
            intent.putExtra(Intent.EXTRA_TEXT, mensajeCorreo)

            try {
                contexto.startActivity(Intent.createChooser(intent, "Enviar correo"))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(contexto, "No hay aplicaciones de correo instaladas", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = "Enviar")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformacionSubMenu(navController: NavController, drawerState: DrawerState, scope: CoroutineScope){
    val options = listOf("¿Qué es Regala Navidad?", "Datos y objetivos", "¿Cómo puedo ayudar?") // Add your sub-options here
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options[0]) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = "Información",
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption, fontSize = 16.sp) },
                    onClick = {
                        when(selectionOption){
                            "¿Qué es Regala Navidad?" -> {
                                selectedOptionText = selectionOption
                                navController.navigate("QueEsScreen")
                            }
                            "Datos y objetivos" -> {
                                selectedOptionText = selectionOption
                                navController.navigate("DatosYObjetivosScreen")
                            }
                            "¿Cómo puedo ayudar?" -> {
                                selectedOptionText = selectionOption
                                navController.navigate("ComoAyudarScreen")
                            }
                        }
                        expanded = false
                        scope.launch { drawerState.close() }
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

@Composable
fun ShowDialog(showDialog: MutableState<Boolean>) {
    val context = LocalContext.current

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                //Cierra el mensaje de alerta cuando el usuario pincha fuera de la pantalla o en el botón de 'Atrás'
                showDialog.value = false
            },
            title = {
                Text(text = "¿Está seguro?")
            },
            text = {
                Text("¿Desea cerrar su sesión?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        auth.signOut()
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Text("Sí, estoy seguro")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text("No")
                }
            }
        )
    }
}

fun drawerAbierto(drawerValue: DrawerValue, mapaAbierto: Boolean): Boolean {
    return drawerValue == DrawerValue.Open || !mapaAbierto
}

suspend fun obtenerPredicciones(textoBusqueda: String): MutableList<SitioRecogida> {
    val sitiosRecogida = mutableListOf<SitioRecogida>()

    val request = FindAutocompletePredictionsRequest.builder()
        .setCountries(listOf("ES"))
        .setQuery(textoBusqueda)
        .build()

    val response = withContext(Dispatchers.IO) {
        Tasks.await(placesClient.findAutocompletePredictions(request))
    }

    val fetchPlaceRequests = response.autocompletePredictions.map { prediction ->
        FetchPlaceRequest.newInstance(prediction.placeId, listOf(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS))
    }

    val deferreds = fetchPlaceRequests.map { fetchPlaceRequest ->
        CoroutineScope(Dispatchers.IO).async {
            try {
                val fetchPlaceResponse = Tasks.await(placesClient.fetchPlace(fetchPlaceRequest))
                val place = fetchPlaceResponse.place
                SitioRecogida(
                    nombreSitio = place.name!!,
                    latitudSitio = place.latLng!!.latitude,
                    longitudSitio = place.latLng!!.longitude,
                    direccionSitio = place.address!!
                )
            } catch (exception: ApiException) {
                Log.e("Error", "Place not found: " + exception.statusCode)
                null
            }
        }
    }

    deferreds.forEach { deferred ->
        val sitioRecogida = deferred.await()
        if (sitioRecogida != null) {
            sitiosRecogida.add(sitioRecogida)
        }
    }

    return sitiosRecogida
}

@Composable
fun ListaSitiosConfirmados(sitiosRecogidaConfirmados: MutableList<SitioRecogida>, isHomePage: Boolean, canEdit: Boolean, onElementoEliminado: (Boolean) -> Unit, onSitioEscogido: (SitioRecogida) -> Unit){
    if(sitiosRecogidaConfirmados.size > 0) {
        LazyColumn {
            items(sitiosRecogidaConfirmados.size) { index ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 5.dp)
                        .let {
                            if (!isHomePage) {
                                it.clickable {
                                    onSitioEscogido(sitiosRecogidaConfirmados[index])
                                }
                            } else it
                        }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = sitiosRecogidaConfirmados[index].nombreSitio, modifier = Modifier.weight(1f))
                        if(canEdit && !isHomePage){
                            IconButton(onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    firestore.eliminaSitioRecogida(sitiosRecogidaConfirmados[index])
                                    onElementoEliminado(true)
                                }
                            },
                                modifier = Modifier.weight(0.3f)) {
                                Icon(Icons.Filled.Delete, contentDescription = "Eliminar sitio")
                            }
                        }
                    }
                }
            }
        }
    }
}
fun checkIfCanEditSitios(rol: String):Boolean{
    return rol == "Coordinador" || rol == "RR.II." || rol == "Logística"
}

fun checkIfCanManageEmails(rol: String):Boolean{
    return rol == "Coordinador" || rol == "Secretaría"
}

/* Si hay tiempo retomamos esta idea (cambio foto perfil)
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