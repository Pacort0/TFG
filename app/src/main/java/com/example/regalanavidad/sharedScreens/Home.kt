package com.example.regalanavidad.sharedScreens

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.example.regalanavidad.BuildConfig.MAPS_API_KEY
import com.example.regalanavidad.R
import com.example.regalanavidad.modelos.CentroEducativo
import com.example.regalanavidad.modelos.DonacionItem
import com.example.regalanavidad.modelos.Evento
import com.example.regalanavidad.modelos.SitioRecogida
import com.example.regalanavidad.modelos.Usuario
import com.example.regalanavidad.organizadorScreens.ExcelScreen
import com.example.regalanavidad.organizadorScreens.OrganizadorHomeScreen
import com.example.regalanavidad.organizadorScreens.RolesTabScreen
import com.example.regalanavidad.organizadorScreens.TareasScreen
import com.example.regalanavidad.organizadorScreens.centroEducativoElegido
import com.example.regalanavidad.viewmodels.TareasViewModel
import com.example.regalanavidad.viewmodels.mapaOrganizadorVM
import com.example.regalanavidad.voluntarioScreens.VoluntarioHomeScreen
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlin.random.Random

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
val eventosConfirmados = mutableListOf<Evento>()
var dineroRecaudado = mutableStateOf(emptyList<DonacionItem>())
const val donacionesSheetId = "11anB2ajRXo049Av60AvUb2lmKxmycjgUK934c5qgXu8"
private lateinit var placesClient: PlacesClient
val tareasVM = TareasViewModel()
class Home : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        val correo = intent.getStringExtra("correo")
        Places.initialize(this, MAPS_API_KEY)
        placesClient = Places.createClient(this)

        runBlocking {
            val task = launch {
                usuario = correo?.let { firestore.getUserByEmail(it) }!!
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
fun TabView(tabBarItems: List<TabBarItem>, navController: NavController, mapaOrganizadorVM: mapaOrganizadorVM, onTabSelected: (String) -> Unit) {
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
                    mapaOrganizadorVM.searchSitioRecogida.value = false
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
            HomeScreen(modifier, navController, mapaOrganizadorVM, onMapaCambiado)
            onMapaCambiado(false)
        }
        "Tareas" -> {
            TareasScreen()
            onMapaCambiado(false)
        }
        "Mail" -> {
            MailScreen(navController)
            onMapaCambiado(false)
        }
        "Mapa" -> {
            MapsScreen(navController, mapaOrganizadorVM)
            onMapaCambiado(true)
        }
        "Roles" -> {
            RolesTabScreen()
            onMapaCambiado(false)
        }
        "Excel" -> {
            ExcelScreen(navController)
            onMapaCambiado(false)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HomeScreen(modifier: Modifier, navController: NavController, mapaOrganizadorVM: mapaOrganizadorVM, onMapaCambiado: (Boolean) -> Unit){
    auth.currentUser?.reload() // Recargamos el usuario para comprobar cualquier actualización

    val context = LocalContext.current
    var textoBusqueda by remember { mutableStateOf("") }
    val firestore = FirestoreManager()
    val scope = CoroutineScope(Dispatchers.Main)
    var agregaSitio by remember { mutableStateOf(false) }
    var muestraListaSitios by remember { mutableStateOf(false) }
    var haySitios by remember { mutableStateOf(false) }
    var recargarSitios by remember { mutableStateOf(true) }
    var sitiosLoading by remember { mutableStateOf(true) }
    var recaudacionsLoading by remember { mutableStateOf(true) }
    val canEditSitios = checkIfCanEditSitios(usuario.nombreRango)
    var navegaSitio by remember { mutableStateOf(false) }
    var muestraListaEventos by remember{ mutableStateOf(false) }
    var agregaEvento by remember{ mutableStateOf(false) }
    var hayEventos by remember { mutableStateOf(false) }
    var recargarEventos by remember { mutableStateOf(true) }
    var eventosLoading by remember { mutableStateOf(true) }
    val canEditEventos = checkIfCanEditEventos(usuario.nombreRango)
    var redactaEmail by remember { mutableStateOf(false) }

    Box(modifier = modifier
        .fillMaxSize()
        .padding(10.dp)){
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
                            onElementoEliminado = {elementoEliminado -> recargarSitios = elementoEliminado},
                            onSitioEscogido = { sitioRecogida -> mapaOrganizadorVM.sitioRecogida.value = sitioRecogida
                                navegaSitio = true
                            }
                        )
                    } else {
                        Text(text = "No hay sitios de recogida confirmados")
                    }
                    if(canEditSitios){
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
            }
            if (agregaSitio) {
                var alturaDialogo by remember { mutableStateOf(150.dp) }
                var buscarSitio by remember{mutableStateOf(false)}
                var prediccionesNuevoSitioRecogida by remember { mutableStateOf<List<SitioRecogida>>(mutableListOf()) }

                Dialog(onDismissRequest = { agregaSitio = false }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(alturaDialogo)
                            .background(Color.LightGray)
                            .padding(35.dp)
                            .clip(RoundedCornerShape(20.dp))
                    ) {
                        Column {
                            OutlinedTextField(
                                value = textoBusqueda,
                                onValueChange = { nuevaBusqueda ->
                                    textoBusqueda = nuevaBusqueda
                                    buscarSitio = true
                                    scope.launch {
                                        prediccionesNuevoSitioRecogida = obtenerPredicciones(nuevaBusqueda)
                                    }
                                },
                                label = { Text("Buscar") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { focusState ->
                                        buscarSitio = focusState.isFocused
                                    }
                            )
                            if(buscarSitio && prediccionesNuevoSitioRecogida.isNotEmpty()){
                                LazyColumn {
                                    alturaDialogo = 400.dp
                                    val topSitios = prediccionesNuevoSitioRecogida.take(4)
                                    items(topSitios.size) { index ->
                                        Card(
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .fillMaxWidth()
                                                .height(70.dp)
                                                .clickable {
                                                    scope.launch(Dispatchers.Main) {
                                                        firestore.insertaSitioRecogida(topSitios[index])
                                                        textoBusqueda = ""
                                                        alturaDialogo = 150.dp
                                                        buscarSitio = false
                                                        recargarSitios = true
                                                        agregaSitio = false
                                                    }
                                                }
                                                .padding(0.dp, 5.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(8.dp)
                                            ) {
                                                if(topSitios[index].nombreSitio != ""){
                                                    LazyRow {
                                                        item{ Text(topSitios[index].nombreSitio, fontSize = 16.sp) }
                                                    }
                                                    LazyRow {
                                                        item { Text(text = topSitios[index].direccionSitio, fontSize = 13.sp) }
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
            }
        }
        if (muestraListaEventos) {
            Dialog(onDismissRequest = { muestraListaEventos = false }) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(35.dp)
                    .clip(RoundedCornerShape(20.dp))) {
                    LazyColumn(modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .background(color = Color.White)) {
                    }
                    if (hayEventos && !eventosLoading){
                        ListaEventosConfirmados(
                            eventosConfirmados,
                            false,
                            canEditSitios,
                            onElementoEliminado = {elementoEliminado -> recargarEventos = elementoEliminado},
                            onEventoEscogido = {
                                    evento -> mapaOrganizadorVM.sitioRecogida.value = evento.lugar
                                navegaSitio = true
                            }
                        )
                    } else {
                        Text(text = "No hay eventos confirmados")
                    }
                    if(canEditEventos){
                        FloatingActionButton(
                            onClick = {
                                agregaEvento = true
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(0.dp, 0.dp, 14.dp, 14.dp)) {
                            Icon(Icons.Filled.Add, contentDescription = "Agregar evento")
                        }
                    }
                }
            }
            if (agregaEvento) {
                var prediccionesNuevoSitioEvento by remember { mutableStateOf<List<SitioRecogida>>(mutableListOf()) }
                var sitioEvento by remember { mutableStateOf(SitioRecogida()) }
                var nombreEvento by remember { mutableStateOf("") }
                var descripcionEvento by remember {mutableStateOf("")}
                var fechaEscogida by remember{mutableStateOf(LocalDate.now()) }
                var horaEscogida by remember{ mutableStateOf(LocalTime.NOON) }
                val fechaFormateada by remember{ derivedStateOf { DateTimeFormatter.ofPattern("dd/MM/yyyy").format(fechaEscogida) } }
                val horaFormateada by remember{ derivedStateOf { DateTimeFormatter.ofPattern("HH:mm").format(horaEscogida) } }
                val fechaDialogState = rememberMaterialDialogState()
                val horaDialogState = rememberMaterialDialogState()
                var alturaDialogo by remember { mutableStateOf(450.dp) }
                var buscarSitio by remember{mutableStateOf(false)}

                Dialog(onDismissRequest = { agregaEvento = false }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(alturaDialogo)
                            .background(Color.LightGray)
                            .padding(35.dp)
                            .clip(RoundedCornerShape(20.dp))
                    ) {
                        Column (
                            Modifier
                                .fillMaxSize()
                                .padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            OutlinedTextField(value = nombreEvento, onValueChange = { nombreEvento = it }, label = { Text("Nombre del evento") })
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(value = descripcionEvento, onValueChange = {descripcionEvento = it}, label = {Text("Descripción del evento")})
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = textoBusqueda,
                                onValueChange = { nuevaBusqueda ->
                                    textoBusqueda = nuevaBusqueda
                                    buscarSitio = true
                                    scope.launch {
                                        prediccionesNuevoSitioEvento = obtenerPredicciones(nuevaBusqueda)
                                    }
                                },
                                label = { Text("Lugar del evento:") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { focusState ->
                                        buscarSitio = focusState.isFocused
                                    }
                            )
                            if(buscarSitio && prediccionesNuevoSitioEvento.isNotEmpty()){
                                LazyColumn {
                                    alturaDialogo = 610.dp
                                    val topSitios = prediccionesNuevoSitioEvento.take(4)
                                    items(topSitios.size) { index ->
                                        Card(
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .height(70.dp)
                                                .fillMaxWidth()
                                                .clickable {
                                                    sitioEvento =
                                                        prediccionesNuevoSitioEvento[index]
                                                    alturaDialogo = 450.dp
                                                    textoBusqueda =
                                                        prediccionesNuevoSitioEvento[index].nombreSitio
                                                    buscarSitio = false
                                                }
                                                .padding(0.dp, 5.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(8.dp)
                                            ) {
                                                if(topSitios[index].nombreSitio != ""){
                                                    LazyRow {
                                                        item{ Text(topSitios[index].nombreSitio, fontSize = 16.sp) }
                                                    }
                                                    LazyRow {
                                                        item { Text(text = topSitios[index].direccionSitio, fontSize = 13.sp) }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = fechaFormateada, Modifier.clickable { fechaDialogState.show() })
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = horaFormateada, Modifier.clickable { horaDialogState.show() })
                        }
                        MaterialDialog(
                            dialogState = fechaDialogState,
                            buttons = {
                                positiveButton("Guardar") {
                                    fechaDialogState.hide()
                                }
                                negativeButton("Cancelar") {
                                    fechaDialogState.hide()
                                }
                            }
                        ) {
                            datepicker(
                                initialDate = LocalDate.now(),
                                title = "Selecciona la fecha del evento",
                                onDateChange = { fechaEscogida = it },
                                allowedDateValidator = { fecha ->
                                    // Permitir solo fechas iguales o posteriores a la fecha actual
                                    !fecha.isBefore(LocalDate.now())
                                }
                            )
                        }

                        MaterialDialog(
                            dialogState = horaDialogState,
                            buttons = {
                                positiveButton("Guardar") {
                                    fechaDialogState.hide()
                                }
                                negativeButton("Cancelar"){
                                    fechaDialogState.hide()
                                }
                            }
                        ) {
                            timepicker(
                                initialTime = LocalTime.NOON,
                                title = "Selecciona la fecha del evento",
                                onTimeChange = { horaEscogida = it },
                                colors = TimePickerDefaults.colors(
                                    activeBackgroundColor = Color.Black, //Fondo horas
                                    inactiveBackgroundColor = Color.LightGray, //Fondo que no está activo
                                    activeTextColor = Color.White, //Color de texto activo
                                    inactiveTextColor = Color.Black, //Color de texto inactivo
                                    inactivePeriodBackground = Color.LightGray, //Fondo de la parte inactiva
                                    selectorColor = Color.Blue, //Selector horas
                                    selectorTextColor = Color.White,
                                    headerTextColor = Color.White, //Titulo
                                    borderColor = Color.White
                                ),
                                is24HourClock = true
                            )
                        }
                        Row (modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(0.dp, 0.dp, 14.dp, 14.dp)) {
                            Button(onClick = { agregaEvento = false}, Modifier.background(Color.Transparent)) {
                                Text(text = "Cancelar")
                            }
                            Button(onClick = {
                                if(sitioEvento.nombreSitio == ""){
                                    Toast.makeText(context, "Selecciona un sitio", Toast.LENGTH_SHORT).show()
                                } else if(nombreEvento == ""){
                                    Toast.makeText(context, "Introduce un nombre del evento", Toast.LENGTH_SHORT).show()
                                } else {
                                    val evento = Evento(
                                        id = generarClaveAleatoria(15),
                                        titulo = nombreEvento,
                                        descripcion = descripcionEvento,
                                        startDate = fechaFormateada,
                                        horaComienzo = horaFormateada,
                                        lugar = sitioEvento
                                    )
                                    scope.launch {
                                        firestore.insertaEvento(evento)
                                        recargarEventos = true
                                        agregaEvento = false
                                    }
                                }
                            }, Modifier.background(Color.Transparent)) {
                                Text(text = "Guardar")
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
                            LaunchedEffect(key1 = Unit) {
                                recaudacionsLoading = true
                                val donacionResponse = getDonationDataFromGoogleSheet(donacionesSheetId, "donaciones")
                                dineroRecaudado.value = donacionResponse.donaciones
                                recaudacionsLoading = false
                            }
                            if (recaudacionsLoading) {
                                Text(text = "Cargando...")
                            } else {
                                Text(text = "Dinero recaudado:")
                                dineroRecaudado.value.forEach { donacion ->
                                    Text(text = "${donacion.tipo}: ${donacion.cantidad}")
                                }
                            }
                        }
                    }
                    Card(modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(5.dp, 0.dp, 0.dp, 0.dp)
                        .clickable { muestraListaSitios = true })
                    {
                        Column {
                            Text(text = "Sitios en los que recogemos: ")
                            LaunchedEffect(key1 = recargarSitios){
                                sitiosLoading = true
                                sitiosRecogidaConfirmados.clear()
                                val sitiosRecogida = firestore.getSitiosRecogida()
                                sitiosRecogida.forEach { sitioRecogida ->
                                    sitiosRecogidaConfirmados.add(sitioRecogida)
                                }
                                haySitios = sitiosRecogidaConfirmados.size != 0
                                recargarSitios = false
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
                                        onElementoEliminado = {elementoEliminado -> recargarSitios = elementoEliminado},
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
                        .padding(5.dp, 0.dp, 0.dp, 0.dp)
                        .clickable { muestraListaEventos = true })
                    {
                        Column {
                            Text(text = "Eventos próximos: ")
                            LaunchedEffect(key1 = recargarEventos){
                                eventosLoading = true
                                eventosConfirmados.clear()
                                val eventos = firestore.getEventos()
                                eventos.forEach { evento ->
                                    eventosConfirmados.add(evento)
                                }
                                hayEventos = eventosConfirmados.size != 0
                                recargarEventos = false
                                eventosLoading = false
                            }
                            if (eventosLoading) {
                                Text(text = "Cargando...")
                            } else {
                                if (hayEventos){
                                    ListaEventosConfirmados(
                                        eventosConfirmados,
                                        true,
                                        canEditSitios,
                                        onElementoEliminado = {elementoEliminado -> recargarEventos = elementoEliminado},
                                        onEventoEscogido = { evento ->
                                            mapaOrganizadorVM.sitioRecogida.value = evento.lugar
                                        }
                                    )
                                } else {
                                    Text(text = "No hay eventos confirmados")
                                }
                            }
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
            agregaEvento = false
            muestraListaEventos = false

            MapsScreen(navController, mapaOrganizadorVM)
            onMapaCambiado(true)
            navController.navigate("Mapa")
            navegaSitio = false
        }
        if(usuario.nombreRango == "Coordinador" || usuario.nombreRango == "Secretaría"){
            FloatingActionButton(
                onClick = { redactaEmail = true },
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(0.dp, 0.dp, 12.dp, 12.dp)
                    .height(45.dp)
                    .width(180.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(painterResource(id = R.drawable.lapiz), contentDescription = "Enviar correo", Modifier.size(30.dp))
                    Text(text = "Redactar correo")
                }
            }
        }
        if(redactaEmail){
            centroEducativoElegido = CentroEducativo()
            navController.navigate("Mail")
            redactaEmail = false
        }
    }
}

@Composable
fun MailScreen(navController: NavController){
    var correoContacto by remember { mutableStateOf(centroEducativoElegido.correoCentro) }
    var asuntoCorreo by remember { mutableStateOf("") }
    var mensajeCorreo by remember { mutableStateOf("") }
    val contexto = LocalContext.current

    Column (
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                correoContacto = ""
                asuntoCorreo = ""
                mensajeCorreo = ""
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(contexto, "No hay aplicaciones de correo instaladas", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = "Enviar")
        }
    }
    BackHandler {
        navController.popBackStack()
        centroEducativoElegido = CentroEducativo()
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
    val sevillaBounds = RectangularBounds.newInstance(
        LatLng(37.0, -6.1), // Suroeste de Sevilla
        LatLng(37.6, -5.5)  // Noreste de Sevilla
    )


    val request = FindAutocompletePredictionsRequest.builder()
        .setCountries(listOf("ES"))
        .setLocationBias(sevillaBounds)
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
}@Composable
fun ListaEventosConfirmados(eventosConfirmados: MutableList<Evento>, isHomePage: Boolean, canEdit: Boolean, onElementoEliminado: (Boolean) -> Unit, onEventoEscogido: (Evento) -> Unit){
    val contexto = LocalContext.current
    if(eventosConfirmados.size > 0) {
        LazyColumn {
            items(eventosConfirmados.size) { index ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 5.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        eventosConfirmados[index].titulo?.let { Text(text = it, modifier = Modifier.weight(1f)) }
                        if(!isHomePage){
                            IconButton(onClick = {
                                val arrayFecha = eventosConfirmados[index].startDate.split("/")
                                val arrayHora = eventosConfirmados[index].horaComienzo.split(":")
                                val startMillis: Long = Calendar.getInstance().run {
                                    set(arrayFecha[2].toInt(), arrayFecha[1].toInt(), arrayFecha[0].toInt(), arrayHora[0].toInt(), arrayHora[1].toInt())
                                    timeInMillis
                                }
                                val endMillis: Long = Calendar.getInstance().run {
                                    set(arrayFecha[2].toInt(), arrayFecha[1].toInt(), arrayFecha[0].toInt(), arrayHora[0].toInt()+2, arrayHora[1].toInt())
                                    timeInMillis
                                }

                                val descripcion = eventosConfirmados[index].descripcion.ifEmpty {
                                    "Evento organizado por Regala Navidad"
                                }

                                Intent(Intent.ACTION_INSERT).apply {
                                    data = CalendarContract.Events.CONTENT_URI
                                    putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                                    putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                                    putExtra(CalendarContract.Events.TITLE, eventosConfirmados[index].titulo)
                                    putExtra(CalendarContract.Events.EVENT_LOCATION, eventosConfirmados[index].lugar.direccionSitio)
                                    putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                                    putExtra(CalendarContract.Events.DESCRIPTION, descripcion)
                                    putExtra(CalendarContract.Events.HAS_ALARM, 1)
                                }.also { intent ->
                                    startActivity(contexto, intent, null)
                                }
                            }, modifier = Modifier.weight(0.3f)) {
                                Icon(Icons.Filled.DateRange, contentDescription = "Añadir al calendario", Modifier.size(25.dp))
                            }
                            IconButton(onClick = {
                                onEventoEscogido(eventosConfirmados[index])
                            },
                                modifier = Modifier.weight(0.3f)) {
                                Icon(painter = painterResource(id = R.drawable.opened_map), contentDescription = "Navegar a mapa", Modifier.size(25.dp))
                            }
                            if(canEdit){
                                IconButton(onClick = {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        firestore.eliminaEvento(eventosConfirmados[index])
                                        onElementoEliminado(true)
                                    }
                                },
                                    modifier = Modifier.weight(0.3f)) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar evento")
                                }
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
    return rol == "Coordinador" || rol == "Secretaría" || rol == "RR.II."
}

fun checkIfCanEditEventos(rol:String):Boolean{
    return rol == "Coordinador" || rol == "RR.II."
}

fun generarClaveAleatoria(longitud: Int): String {
    val caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    var id: String

    do {
        id = List(longitud) {
            caracteres[Random.nextInt(caracteres.length)]
        }.joinToString("")
    } while (eventosConfirmados.any { it.id == id })

    return id
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