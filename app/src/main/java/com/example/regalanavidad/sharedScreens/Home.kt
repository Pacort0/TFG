package com.example.regalanavidad.sharedScreens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.example.regalanavidad.BuildConfig.MAPS_API_KEY
import com.example.regalanavidad.R
import com.example.regalanavidad.modelos.CentroEducativo
import com.example.regalanavidad.modelos.DonacionItem
import com.example.regalanavidad.modelos.Evento
import com.example.regalanavidad.modelos.SitioRecogida
import com.example.regalanavidad.modelos.TabBarItem
import com.example.regalanavidad.modelos.Usuario
import com.example.regalanavidad.organizadorScreens.ExcelScreen
import com.example.regalanavidad.organizadorScreens.MailScreen
import com.example.regalanavidad.organizadorScreens.OrganizadorHomeScreen
import com.example.regalanavidad.organizadorScreens.RolesTabScreen
import com.example.regalanavidad.organizadorScreens.TareasScreen
import com.example.regalanavidad.organizadorScreens.centroEducativoElegido
import com.example.regalanavidad.ui.theme.BordeIndvCards
import com.example.regalanavidad.ui.theme.FondoApp
import com.example.regalanavidad.ui.theme.FondoIndvCards
import com.example.regalanavidad.ui.theme.FondoMenus
import com.example.regalanavidad.ui.theme.FondoTarjetaInception
import com.example.regalanavidad.viewmodels.EventosVM
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
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.system.exitProcess

val drawerItems = listOf("Información", "Contáctanos", "Patrocinadores", "Otros años")
val auth = Firebase.auth
var usuario = Usuario()
val firestore = FirestoreManager()
private val sitiosRecogidaConfirmados = mutableListOf<SitioRecogida>()
private val eventosConfirmados = mutableListOf<Evento>()
var dineroRecaudado = mutableStateOf(emptyList<DonacionItem>())
const val donacionesSheetId = "11anB2ajRXo049Av60AvUb2lmKxmycjgUK934c5qgXu8"
private lateinit var placesClient: PlacesClient

@RequiresApi(Build.VERSION_CODES.O)
val eventosVM = EventosVM()

//Para las redes sociales
val customFontFamily = FontFamily(
    Font(R.font.snackercomic, FontWeight.Normal)
)
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

    NavigationBar (
        modifier = Modifier.height(80.dp),
        containerColor = FondoMenus
    ) {
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
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = FondoApp
                ),
                icon = {
                    TabBarIconView(
                        isSelected = selectedTabIndex == index,
                        selectedIcon = tabBarItem.selectedIcon,
                        unselectedIcon = tabBarItem.unselectedIcon,
                        title = tabBarItem.title,
                        badgeAmount = tabBarItem.badgeAmount
                    )
                },
                label = {Text(tabBarItem.title, color = Color.Black)})
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
            contentDescription = title,
            tint = Color.Black
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
            TareasScreen(navController)
            onMapaCambiado(true)
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
            RolesTabScreen(navController)
            onMapaCambiado(false)
        }
        "Excel" -> {
            ExcelScreen(navController, onMapaCambiado)
            onMapaCambiado(false)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun HomeScreen(modifier: Modifier, navController: NavController, mapaOrganizadorVM: mapaOrganizadorVM, onMapaCambiado: (Boolean) -> Unit){
    auth.currentUser?.reload() // Recargamos el usuario para comprobar cualquier actualización

    val context = LocalContext.current
    var textoBusqueda by remember { mutableStateOf("") }
    val firestore = FirestoreManager()
    val scope = CoroutineScope(Dispatchers.Main)
    var agregaSitio by remember { mutableStateOf(false) }
    var haySitios by remember { mutableStateOf(false) }
    var recargarSitios by remember { mutableStateOf(true) }
    var sitiosLoading by remember { mutableStateOf(true) }
    var recaudacionsLoading by remember { mutableStateOf(true) }
    val canEditSitios = checkIfCanEditSitios(usuario.nombreRango)
    var navegaSitio by remember { mutableStateOf(false) }
    var agregaEvento by remember{ mutableStateOf(false) }
    var hayEventos by remember { mutableStateOf(false) }
    var recargarEventos by remember { mutableStateOf(true) }
    var eventosLoading by remember { mutableStateOf(true) }
    val canEditEventos = checkIfCanEditEventos(usuario.nombreRango)
    var redactaEmail by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { 4 }
    )
    var showCloseAppDialog by remember {mutableStateOf(false)}
    val eventoVM = eventosVM.proximoEvento.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var prediccionesNuevoSitioRecogida by remember { mutableStateOf<List<SitioRecogida>>(mutableListOf())}
    var prediccionesNuevoSitioEvento by remember { mutableStateOf<List<SitioRecogida>>(mutableListOf()) }

    if (textoBusqueda == "" && prediccionesNuevoSitioRecogida.isNotEmpty()){
        prediccionesNuevoSitioRecogida = emptyList()
    }

    Box(modifier = modifier
        .fillMaxSize()
        .background(FondoApp)
    ){
        if (agregaSitio) {
            var alturaDialogo by remember { mutableStateOf(180.dp) }
            var buscarSitio by remember{mutableStateOf(false)}
             Dialog(onDismissRequest = {
                 textoBusqueda = ""
                 agregaSitio = false
             }) {
                 Box(
                     modifier = Modifier
                         .fillMaxWidth()
                         .height(alturaDialogo.let { if (prediccionesNuevoSitioRecogida.isNotEmpty()) 480.dp else it })
                         .background(FondoApp)
                         .padding(35.dp)
                 ) {
                     Column (
                         Modifier
                             .fillMaxWidth()
                             .wrapContentHeight()
                             .padding(6.dp),
                         horizontalAlignment = Alignment.CenterHorizontally,
                         verticalArrangement = Arrangement.Center
                     ) {
                         Text(text = "Nuevo sitio", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                         Spacer(modifier = Modifier.height(16.dp))
                         OutlinedTextField(
                             value = textoBusqueda,
                             textStyle = TextStyle(color = Color.Black),
                             onValueChange = { nuevaBusqueda ->
                                 textoBusqueda = nuevaBusqueda
                                 buscarSitio = true
                                 scope.launch {
                                     prediccionesNuevoSitioRecogida = obtenerPredicciones(nuevaBusqueda)
                                 } },
                             label = { Text("Buscar sitio", color = Color.Black) },
                             leadingIcon = {
                                 Icon(
                                     painter = painterResource(id = R.drawable.lupa),
                                     contentDescription = "Lupa",
                                     modifier = Modifier.size(24.dp),
                                     tint = Color.Black
                                 ) },
                             modifier = Modifier
                                 .fillMaxWidth()
                                 .onFocusChanged { focusState ->
                                     buscarSitio = focusState.isFocused
                                 },
                             colors = OutlinedTextFieldDefaults.colors(
                                 focusedContainerColor = FondoIndvCards,
                                 unfocusedContainerColor = FondoIndvCards,
                                 focusedBorderColor = BordeIndvCards,
                                 unfocusedBorderColor = BordeIndvCards
                             ))
                         if (textoBusqueda == "" && prediccionesNuevoSitioRecogida.isNotEmpty()){
                             prediccionesNuevoSitioRecogida = emptyList()
                         }
                         if(buscarSitio && prediccionesNuevoSitioRecogida.isNotEmpty()){
                             LazyColumn {
                                 val topSitios = prediccionesNuevoSitioRecogida.take(4)
                                 items(topSitios.size) { index ->
                                     Card(
                                         modifier = Modifier
                                             .padding(top = 5.dp, end = 5.dp)
                                             .height(70.dp)
                                             .fillMaxWidth()
                                             .clip(CircleShape)
                                             .border(1.dp, BordeIndvCards, CircleShape)
                                             .background(FondoIndvCards)
                                             .clickable {
                                                 scope.launch(Dispatchers.IO) {
                                                     firestore.insertaSitioRecogida(topSitios[index])
                                                     textoBusqueda = ""
                                                     alturaDialogo = 150.dp
                                                     buscarSitio = false
                                                     recargarSitios = true
                                                     agregaSitio = false
                                                 }
                                             }
                                             .padding(0.dp, 5.dp),
                                         colors = CardDefaults.cardColors(
                                             containerColor = FondoIndvCards
                                         )
                                     ) {
                                         Column(
                                             modifier = Modifier.padding(8.dp)
                                         ) {
                                             if(topSitios[index].nombreSitio != ""){
                                                    LazyRow {
                                                        item{ Text(topSitios[index].nombreSitio, fontSize = 16.sp, color = Color.Black) }
                                                    }
                                                 LazyRow {
                                                     item { Text(text = topSitios[index].direccionSitio, fontSize = 13.sp, color = Color.Black) }
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

            if (agregaEvento) {
                var sitioEvento by remember { mutableStateOf(SitioRecogida()) }
                var nombreEvento by remember { mutableStateOf("") }
                var descripcionEvento by remember {mutableStateOf("")}
                var fechaEscogida by remember{mutableStateOf(LocalDate.now()) }
                var horaEscogida by remember{ mutableStateOf(LocalTime.NOON) }
                val fechaFormateada by remember{ derivedStateOf { DateTimeFormatter.ofPattern("dd/MM/yyyy").format(fechaEscogida) } }
                val horaFormateada by remember{ derivedStateOf { DateTimeFormatter.ofPattern("HH:mm").format(horaEscogida) } }
                val fechaDialogState = rememberMaterialDialogState()
                val horaDialogState = rememberMaterialDialogState()
                val alturaDialogo by remember { mutableStateOf(420.dp) }
                var buscarSitio by remember{mutableStateOf(false)}


                Dialog(onDismissRequest = { agregaEvento = false }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(alturaDialogo.let { if (prediccionesNuevoSitioEvento.isNotEmpty()) 610.dp else it })
                            .background(FondoApp)
                            .padding(top = 15.dp, start = 30.dp, end = 30.dp, bottom = 15.dp)
                            .clip(RoundedCornerShape(20.dp))
                    ) {
                        Column (
                            Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "Nuevo evento", fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Spacer(modifier = Modifier.height(16.dp))
                            OutlinedTextField(
                                value = nombreEvento,
                                onValueChange = { nombreEvento = it },
                                textStyle = TextStyle(color = Color.Black),
                                label = { Text("Nombre del evento", color = Color.Black) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = FondoIndvCards,
                                    unfocusedContainerColor = FondoIndvCards,
                                    cursorColor = Color.Black,
                                    focusedBorderColor = BordeIndvCards,
                                    unfocusedBorderColor = BordeIndvCards
                                ))
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = descripcionEvento,
                                onValueChange = {descripcionEvento = it},
                                textStyle = TextStyle(color = Color.Black),
                                label = {Text("Descripción del evento", color = Color.Black)},
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = FondoIndvCards,
                                    unfocusedContainerColor = FondoIndvCards,
                                    cursorColor = Color.Black,
                                    focusedBorderColor = BordeIndvCards,
                                    unfocusedBorderColor = BordeIndvCards
                                ))
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = textoBusqueda,
                                textStyle = TextStyle(color = Color.Black),
                                onValueChange = { nuevaBusqueda ->
                                    textoBusqueda = nuevaBusqueda
                                    buscarSitio = true
                                    scope.launch {
                                        prediccionesNuevoSitioEvento = obtenerPredicciones(nuevaBusqueda)
                                    }
                                },
                                label = { Text("Lugar del evento", color = Color.Black) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { focusState ->
                                        if (!focusState.isFocused) {
                                            prediccionesNuevoSitioEvento = emptyList()
                                        }
                                        buscarSitio = focusState.isFocused
                                    },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = FondoIndvCards,
                                    unfocusedContainerColor = FondoIndvCards,
                                    cursorColor = Color.Black,
                                    focusedBorderColor = BordeIndvCards,
                                    unfocusedBorderColor = BordeIndvCards
                                )
                            )
                            if (textoBusqueda == "" && prediccionesNuevoSitioEvento.isNotEmpty()){
                                prediccionesNuevoSitioEvento = emptyList()
                            }
                            if(buscarSitio && prediccionesNuevoSitioEvento.isNotEmpty()){
                                LazyColumn {
                                    val topSitios = prediccionesNuevoSitioEvento.take(4)
                                    items(topSitios.size) { index ->
                                        Card(
                                            modifier = Modifier
                                                .padding(top = 5.dp, end = 5.dp)
                                                .height(70.dp)
                                                .fillMaxWidth()
                                                .clip(CircleShape)
                                                .border(1.dp, BordeIndvCards, CircleShape)
                                                .background(FondoIndvCards)
                                                .clickable {
                                                    sitioEvento =
                                                        prediccionesNuevoSitioEvento[index]
                                                    textoBusqueda =
                                                        prediccionesNuevoSitioEvento[index].nombreSitio
                                                    buscarSitio = false
                                                }
                                                .padding(0.dp, 5.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = FondoIndvCards
                                            )
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(8.dp)
                                            ) {
                                                if(topSitios[index].nombreSitio != ""){
                                                    LazyRow {
                                                        item{ Text(topSitios[index].nombreSitio, fontSize = 16.sp, color = Color.Black) }
                                                    }
                                                    LazyRow {
                                                        item { Text(text = topSitios[index].direccionSitio, fontSize = 13.sp, color = Color.Black) }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (prediccionesNuevoSitioEvento.isEmpty()){
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = fechaFormateada,
                                    Modifier.clickable { fechaDialogState.show() },
                                    color = Color.Black)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = horaFormateada,
                                    Modifier.clickable { horaDialogState.show() },
                                    color = Color.Black)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
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
                        if (prediccionesNuevoSitioEvento.isEmpty()){
                            Row (modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(0.dp, 0.dp, 0.dp, 14.dp)) {
                                Button(
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BordeIndvCards
                                    ),
                                    onClick = {
                                        textoBusqueda = ""
                                        agregaEvento = false },
                                    modifier = Modifier.background(Color.Transparent))
                                {
                                    Text(text = "Cancelar", color = Color.Black)
                                }
                                Button(
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BordeIndvCards
                                    ),
                                    onClick = {
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
                                    },
                                    modifier = Modifier.background(Color.Transparent)) {
                                    Text(text = "Guardar", color = Color.Black)
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
//            Text(
//                text = "Hola ${usuario.nombre}!",
//                modifier = modifier.padding(0.dp, 10.dp)
//            )

            HorizontalPager(
                state = pagerState,
                beyondBoundsPageCount = 1,
                contentPadding = PaddingValues(6.dp, 0.dp, 6.dp, 0.dp),
                pageSpacing = 5.dp,
                verticalAlignment = Alignment.CenterVertically
            ) { page ->
                when (page) {
                    0 -> {
                        Card(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth()
                                .graphicsLayer {
                                    val pageOffset =
                                        ((pagerState.currentPage) + pagerState.currentPageOffsetFraction).absoluteValue
                                    val scale = lerp(0.7f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                                    alpha = lerp(
                                        start = 0.5f,
                                        stop = 1f,
                                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                    )
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .padding(5.dp, 0.dp, 0.dp, 0.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent
                            )
                        ) {
                            LaunchedEffect(key1 = recargarSitios) {
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
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .fillMaxSize()
                                .background(color = Color.Transparent)) {
                                if(sitiosLoading){
                                    Column (
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Transparent),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                    ) {
                                        CircularProgressIndicator(
                                            color = BordeIndvCards
                                        )
                                        Text(
                                            text = "Cargando sitios...",
                                            modifier = Modifier.padding(top = 8.dp),
                                            color = Color.Black
                                        )
                                    }
                                } else {
                                    Column(
                                        Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ){
                                        Row (
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Box(modifier = Modifier.fillMaxWidth()) {
                                                Text(
                                                    text = "Sitios de recogida",
                                                    color = Color.Black,
                                                    textAlign = TextAlign.Center,
                                                    fontSize = 24.sp,
                                                    modifier = Modifier.align(Alignment.Center),
                                                )
                                                if (canEditSitios) {
                                                    IconButton(
                                                        onClick = { agregaSitio = true },
                                                        modifier = Modifier
                                                            .size(58.dp)
                                                            .align(Alignment.CenterEnd)
                                                            .padding(end = 20.dp))
                                                            {
                                                                Icon(
                                                                    Icons.Filled.AddCircle,
                                                                    "Agregar sitio",
                                                                    Modifier.fillMaxSize(),
                                                                    Color.Black
                                                                )
                                                            }
                                                    }
                                                }
                                        }
                                        if(haySitios){
                                            ListaSitiosConfirmados(sitiosRecogidaConfirmados,
                                                false,
                                                canEditSitios,
                                                onElementoEliminado = {elementoEliminado -> recargarSitios = elementoEliminado},
                                                onSitioEscogido = { sitioRecogida -> mapaOrganizadorVM.sitioRecogida.value = sitioRecogida
                                                    navegaSitio = true
                                                }
                                            )
                                        } else {
                                            Text(text = "No hay sitios de recogida confirmados", color = Color.Black)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    1 -> {
                        Card(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth()
                                .graphicsLayer {
                                    val pageOffset =
                                        ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                                    val scale = lerp(0.7f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                                    alpha = lerp(
                                        start = 0.5f,
                                        stop = 1f,
                                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                    )
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .padding(5.dp, 0.dp, 0.dp, 0.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent
                            )
                        ) {
                            Box (
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color = Color.Transparent)
                            ) {
                                LaunchedEffect(key1 = recargarEventos) {
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
                                    Column (
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator(
                                            color = BordeIndvCards
                                        )
                                        Text(
                                            text = "Cargando eventos...",
                                            modifier = Modifier.padding(top = 8.dp),
                                            color = Color.Black
                                        )
                                    }
                                } else {
                                    if (hayEventos) {
                                        Column (
                                            Modifier.fillMaxSize(),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ){
                                            Row (
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(5.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Box(modifier = Modifier.fillMaxWidth()) {
                                                    Text(
                                                        text = "Eventos próximos",
                                                        textAlign = TextAlign.Center,
                                                        fontSize = 24.sp,
                                                        modifier = Modifier.align(Alignment.Center),
                                                        color = Color.Black
                                                        )
                                                    if (canEditEventos) {
                                                        IconButton(
                                                            onClick = { agregaEvento = true },
                                                            modifier = Modifier
                                                                .size(58.dp)
                                                                .align(Alignment.CenterEnd)
                                                                .padding(end = 20.dp)                                                            )
                                                        {
                                                            Icon(
                                                                Icons.Filled.AddCircle,
                                                                "Agregar evento",
                                                                Modifier.fillMaxSize(),
                                                                tint = Color.Black
                                                            )
                                                        }
                                                    }
                                                }
                                            }
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
                                        }
                                    }
                                    else {
                                        Text(text = "No hay eventos confirmados", color = Color.Black)
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Transparent)
                                .graphicsLayer {
                                    val pageOffset =
                                        ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                                    val scale = lerp(0.7f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                                    alpha = lerp(
                                        start = 0.5f,
                                        stop = 1f,
                                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                    )
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .padding(5.dp, 0.dp, 0.dp, 0.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent
                            )
                        ) {
                            Column (
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .background(Color.Transparent))
                            {
                                Row (
                                    Modifier
                                        .weight(0.5f)
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedCard(
                                        onClick = {},
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Box (
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.dinero_recaudado_wp),
                                                contentDescription = "Background Ig Image",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            Row(
                                                Modifier
                                                    .fillMaxSize()
                                                    .padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Column (
                                                    Modifier.fillMaxSize()
                                                ) {
                                                    LaunchedEffect(key1 = Unit) {
                                                        recaudacionsLoading = true
                                                        val donacionResponse = getDonationDataFromGoogleSheet(
                                                            donacionesSheetId,
                                                            "donaciones"
                                                        )
                                                        dineroRecaudado.value = donacionResponse.donaciones
                                                        recaudacionsLoading = false
                                                    }
                                                    if (recaudacionsLoading) {
                                                        Text(text = "Cargando...")
                                                    } else {
                                                        Row (
                                                            Modifier
                                                                .fillMaxWidth()
                                                                .weight(0.1f),
                                                            horizontalArrangement = Arrangement.Center
                                                        ) {
                                                            Text(text = "Dinero recaudado:",
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 24.sp,
                                                                color = Color.White)
                                                        }
                                                        dineroRecaudado.value.forEach { donacion ->
                                                            Row (
                                                                modifier
                                                                    .fillMaxSize()
                                                                    .weight(
                                                                        if (donacion.tipo == "TOTAL") 0.3f else 0.2f
                                                                    )
                                                            ) {
                                                                Card(
                                                                    modifier = Modifier
                                                                        .fillMaxSize()
                                                                        .padding(8.dp),
                                                                    shape = MaterialTheme.shapes.medium,
                                                                    colors = CardDefaults.cardColors(
                                                                        containerColor = Color.Transparent
                                                                    )
                                                                ) {
                                                                    Box(
                                                                        modifier = modifier
                                                                            .graphicsLayer {
                                                                                alpha =
                                                                                    0.99f // Necesario para activar el desenfoque en Android 12 y anteriores
                                                                            }
                                                                            .drawBehind {
                                                                                drawIntoCanvas { canvas ->
                                                                                    val paint =
                                                                                        Paint()
                                                                                    val frameworkPaint =
                                                                                        paint.asFrameworkPaint()
                                                                                    frameworkPaint.color =
                                                                                        0x99FFFFFF.toInt()
                                                                                    frameworkPaint.maskFilter =
                                                                                        android.graphics.BlurMaskFilter(
                                                                                            30f,
                                                                                            android.graphics.BlurMaskFilter.Blur.NORMAL
                                                                                        )
                                                                                    canvas.nativeCanvas.apply {
                                                                                        drawRect(
                                                                                            0f,
                                                                                            0f,
                                                                                            size.width,
                                                                                            size.height,
                                                                                            frameworkPaint
                                                                                        )
                                                                                    }
                                                                                }
                                                                            }
                                                                    ){
                                                                        Box (modifier = Modifier.fillMaxSize()){
                                                                            when(donacion.tipo) {
                                                                                "BIZUM" -> DonacionRow(donacion, R.drawable.bizum)
                                                                                "EFECTIVO" -> DonacionRow(donacion, R.drawable.dinero_efectivo)
                                                                                "TRANSFERENCIA" -> DonacionRow(donacion, R.drawable.transferencia)
                                                                                "TOTAL" -> DonacionRow(donacion, R.drawable.total)
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
                                Row (
                                    Modifier
                                        .weight(0.5f),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable {
                                                coroutineScope.launch(Dispatchers.IO) {
                                                    pagerState.animateScrollToPage(1)
                                                }
                                            },
                                        colors = CardDefaults.cardColors(
                                            containerColor = Color.Transparent
                                        )
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.dia_calendario),
                                                contentDescription = "Background Ig Image",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            Column (
                                                Modifier
                                                    .fillMaxSize()
                                                    .padding(
                                                        top = 68.dp,
                                                        start = 5.dp,
                                                        end = 5.dp,
                                                        bottom = 0.dp
                                                    )
                                            ) {
                                                if (eventoVM.value.titulo != "") {
                                                    Row (
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .wrapContentHeight()
                                                            .weight(0.3f),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        Column (
                                                            Modifier.fillMaxSize(),
                                                            verticalArrangement = Arrangement.Center,
                                                            horizontalAlignment = Alignment.CenterHorizontally
                                                        ){
                                                            Text(
                                                                text = "Próximo evento:",
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 28.sp,
                                                                color = Color.Black
                                                            )
                                                            eventoVM.value.titulo.let {
                                                                Text(
                                                                    text = it,
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontSize = 18.sp,
                                                                    color = Color.Black
                                                                )
                                                            }
                                                        }
                                                    }
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .weight(0.45f),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        eventoVM.value.startDate.let {
                                                            val valoresFecha = eventoVM.value.startDate.split("/")
                                                            Column (
                                                                Modifier.fillMaxSize(),
                                                                verticalArrangement = Arrangement.Center,
                                                                horizontalAlignment = Alignment.CenterHorizontally
                                                            ) {
                                                                Text(
                                                                    text = valoresFecha[0],
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontSize = 60.sp,
                                                                    color = Color.Black
                                                                )
                                                                Spacer(modifier = Modifier.height(5.dp))
                                                                Text(
                                                                    text = cambiaNumeroPorMes(
                                                                        valoresFecha[1]
                                                                    ),
                                                                    fontWeight = FontWeight.Bold,
                                                                    fontSize = 20.sp,
                                                                    color = Color.Black
                                                                )
                                                            }
                                                        }
                                                    }
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .weight(0.25f),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        eventoVM.value.lugar.let {
                                                            Text(
                                                                text = it.nombreSitio,
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 20.sp,
                                                                color = Color.Black
                                                            )
                                                        }
                                                    }
                                                } else {
                                                    Row (
                                                        modifier = Modifier.fillMaxSize(),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        Text(
                                                            text = "No hay eventos próximos",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 20.sp,
                                                            color = Color.Black
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    3 -> {
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Transparent)
                                .graphicsLayer {
                                    val pageOffset =
                                        ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                                    val scale = lerp(0.7f, 1f, 1f - pageOffset.coerceIn(0f, 1f))
                                    alpha = lerp(
                                        start = 0.5f,
                                        stop = 1f,
                                        fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                    )
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .padding(5.dp, 0.dp, 0.dp, 0.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent
                            )
                        ) {
                            Column (
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .background(Color.Transparent))
                            {
                                Row (
                                    Modifier
                                        .weight(0.5f)
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedCard(
                                        onClick = {
                                            val uri = Uri.parse("https://www.instagram.com/_u/proyectoregalanavidad")
                                            val intent = Intent(Intent.ACTION_VIEW, uri)
                                            intent.setPackage("com.instagram.android")
                                            try {
                                                startActivity(context, intent, null)
                                            } catch (e: ActivityNotFoundException) {
                                                Log.e("Error", "Instagram no está instalado")
                                                startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/_u/proyectoregalanavidad")), null)
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .border(1.dp, Color.Black, RoundedCornerShape(20.dp))
                                    ) {
                                        CartaRSS(R.drawable.logo_ig, "Instagram")
                                    }
                                }
                                Row (
                                    Modifier
                                        .weight(0.5f)
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedCard(onClick = {
                                        val uri = Uri.parse("https://www.tiktok.com/@agrupacionrutadehercules")
                                        val intent = Intent(Intent.ACTION_VIEW, uri)
                                        intent.setPackage("com.tiktok.android")
                                        try {
                                            startActivity(context, intent, null)
                                        } catch (e: ActivityNotFoundException) {
                                            Log.e("Error", "Tiktok no está instalado")
                                            startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@agrupacionrutadehercules")), null)
                                        } },
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .border(1.dp, Color.Black, RoundedCornerShape(20.dp))
                                    ) {
                                        CartaRSS(R.drawable.logo_tiktok, "TikTok")
                                    }
                                }
                                Row (
                                    Modifier
                                        .weight(0.5f)
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedCard(onClick = {
                                        val uri = Uri.parse("https://chat.whatsapp.com/KCDMPKRZTlA3XaaMnbdrXa")
                                        val intent = Intent(Intent.ACTION_VIEW, uri)
                                        intent.setPackage("com.whatsapp.android")
                                        try {
                                            startActivity(context, intent, null)
                                        } catch (e: ActivityNotFoundException) {
                                            Log.e("Error", "Whatsapp no está instalado")
                                            startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse("https://chat.whatsapp.com/KCDMPKRZTlA3XaaMnbdrXa")), null)
                                        } },
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .border(1.dp, Color.Black, RoundedCornerShape(20.dp))
                                    ) {
                                        CartaRSS(R.drawable.logo_whatsapp, "WhatsApp")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }
        if(navegaSitio){
            mapaOrganizadorVM.searchSitioRecogida.value = true
            agregaSitio = false
            agregaEvento = false

            MapsScreen(navController, mapaOrganizadorVM)
            onMapaCambiado(true)
            navController.navigate("Mapa")
            navegaSitio = false
        }
        if(usuario.nombreRango == "Coordinador" || usuario.nombreRango == "RR.II."){
            FloatingActionButton(
                onClick = { redactaEmail = true },
                containerColor = FondoMenus,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp, 0.dp, 0.dp, 35.dp)
                    .height(40.dp)
                    .width(if (pagerState.currentPage == 0 || pagerState.currentPage == 1) 160.dp else 40.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(painterResource(id = R.drawable.lapiz), contentDescription = "Enviar correo", Modifier.size(25.dp), tint = Color.Black)
                    if (pagerState.currentPage == 0 || pagerState.currentPage == 1){
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = "Redactar correo", color = Color.Black)
                    }
                }
            }
        }
        if(redactaEmail){
            centroEducativoElegido = CentroEducativo()
            navController.navigate("Mail")
            redactaEmail = false
        }
    }
    if (showCloseAppDialog){
        AlertDialog(
            containerColor = FondoApp,
            onDismissRequest = {
                showCloseAppDialog = false
            },
            title = {
                Text(text = "¿Cerrar la aplicación?", color = Color.Black)
            },
            text = {
                Text("¿Desea cerrar la aplicación?", color = Color.Black)
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BordeIndvCards
                    ),
                    onClick = {
                        ActivityCompat.finishAffinity(context as Activity)
                        exitProcess(0)
                    }
                ) {
                    Text("Sí, estoy seguro", color = Color.Black)
                }
            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BordeIndvCards
                    ),
                    onClick = {
                        showCloseAppDialog = false
                    }
                ) {
                    Text("No", color = Color.Black)
                }
            }
        )
    }
    BackHandler {
        val paginaPreviaPila = navController.previousBackStackEntry
        if(paginaPreviaPila != null){
            if(paginaPreviaPila.destination.route != "inicio" || paginaPreviaPila.destination.route != "waitingScreen"){
                navController.popBackStack()
            } else {
                showCloseAppDialog = true
            }
        } else {
            showCloseAppDialog = true
        }
    }
}
@Composable
fun DonacionRow(donacion: DonacionItem, imageResId: Int) {
    Row(
        Modifier
            .fillMaxSize()
            .padding(if (donacion.tipo == "TOTAL") 5.dp else 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = "Imagen Donativo",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .weight(0.4f)
                .padding(end = 5.dp)
        )
        Text(
            text = donacion.cantidad,
            color = Color(44, 173, 18),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.6f)
        )
    }
}
@Composable
private fun CartaRSS(idLogo: Int, nombreRRSS: String) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (idLogo){
            R.drawable.logo_ig -> {
                Image(
                    painter = painterResource(id = R.drawable.ig_bg),
                    contentDescription = "Background Ig Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            R.drawable.logo_tiktok -> {
                Image(
                    painter = painterResource(id = R.drawable.tiktok_bg),
                    contentDescription = "Background TikTok Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            R.drawable.logo_whatsapp -> {
                Image(
                    painter = painterResource(id = R.drawable.whatsapp_bg),
                    contentDescription = "Background Ig Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Row(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                Modifier
                    .weight(0.5f)
                    .background(Color.Transparent),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = idLogo),
                    contentDescription = "Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(8.dp)
                )
            }
            Column(
                Modifier
                    .weight(0.5f)
                    .background(Color.Transparent),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = nombreRRSS,
                    color = Color.White,
                    fontFamily = customFontFamily,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp)
                )
            }
        }
    }
}

private fun cambiaNumeroPorMes(numero: String): String {
    return when(numero){
        "01" -> "Enero"
        "02" -> "Febrero"
        "03" -> "Marzo"
        "04" -> "Abril"
        "05" -> "Mayo"
        "06" -> "Junio"
        "07" -> "Julio"
        "08" -> "Agosto"
        "09" -> "Septiembre"
        "10" -> "Octubre"
        "11" -> "Noviembre"
        "12" -> "Diciembre"
        else -> "Error"
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
fun CierraSesionDialog(showDialog: MutableState<Boolean>, navController: NavController) {
    val context = LocalContext.current

    if (showDialog.value) {
        AlertDialog(
            containerColor = FondoApp,
            onDismissRequest = {
                //Cierra el mensaje de alerta cuando el usuario pincha fuera de la pantalla o en el botón de 'Atrás'
                showDialog.value = false
            },
            title = {
                Text(text = "¿Está seguro?", color = Color.Black)
            },
            text = {
                Text("¿Desea cerrar su sesión?", color = Color.Black)
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BordeIndvCards
                    ),
                    onClick = {
                        showDialog.value = false
                        auth.signOut()
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                        navController.popBackStack()
                    }
                ) {
                    Text("Sí, estoy seguro", color = Color.Black)
                }
            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BordeIndvCards
                    ),
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text("No", color = Color.Black)
                }
            }
        )
    }
}

fun drawerAbierto(drawerValue: DrawerValue, mapaAbierto: Boolean): Boolean {
    return drawerValue == DrawerValue.Open || (!mapaAbierto)
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
    var showEliminarDialog by remember { mutableStateOf(false) }
    var indexActual by remember { mutableIntStateOf(0) }
    if(sitiosRecogidaConfirmados.size > 0) {
        LazyColumn {
            items(sitiosRecogidaConfirmados.size) { index ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .clip(CircleShape)
                        .border(1.dp, BordeIndvCards, CircleShape)
                        .let {
                            if (!isHomePage) {
                                it.clickable {
                                    onSitioEscogido(sitiosRecogidaConfirmados[index])
                                }
                            } else it
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = FondoIndvCards
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = sitiosRecogidaConfirmados[index].nombreSitio,
                            color = Color.Black,
                            modifier = Modifier.weight(1f))
                        if(canEdit && !isHomePage){
                            IconButton(onClick = {
                                indexActual = index
                                showEliminarDialog = true
                            },
                                modifier = Modifier.weight(0.3f)) {
                                Icon(Icons.Filled.Delete, contentDescription = "Eliminar sitio", tint = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    } else {
        Text(text = "No hay sitios de recogida confirmados", color = Color.Black)
    }
    if (showEliminarDialog) {
        AlertDialog(
            containerColor = FondoApp,
            onDismissRequest = {
                showEliminarDialog = false
            },
            title = {
                Text(text = "¿Está seguro?", color = Color.Black)
            },
            text = {
                Text("¿Desea eliminar este sitio?", color = Color.Black)
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BordeIndvCards
                    ),
                    onClick = {
                        showEliminarDialog = false
                        CoroutineScope(Dispatchers.IO).launch {
                            firestore.eliminaSitioRecogida(sitiosRecogidaConfirmados[indexActual])
                            onElementoEliminado(true)
                        }
                    }
                ) {
                    Text("Eliminar", color = Color.Black)
                }
            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BordeIndvCards
                    ),
                    onClick = {
                        showEliminarDialog = false
                    }
                ) {
                    Text("No", color = Color.Black)
                }
            }
        )
    }
}
@Composable
fun ListaEventosConfirmados(eventosConfirmados: MutableList<Evento>, isHomePage: Boolean, canEdit: Boolean, onElementoEliminado: (Boolean) -> Unit, onEventoEscogido: (Evento) -> Unit){
    var showDialog by remember { mutableStateOf(false) }
    var indexActual by remember { mutableIntStateOf(0) }
    val contexto = LocalContext.current
    if(eventosConfirmados.size > 0) {
        LazyColumn(
            verticalArrangement = Arrangement.Center
        ) {
            items(eventosConfirmados.size) { index ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .clip(CircleShape)
                        .border(1.dp, BordeIndvCards, CircleShape),
                    colors = CardDefaults.cardColors(
                        containerColor = FondoIndvCards
                    )
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = eventosConfirmados[index].titulo,
                                modifier = Modifier
                                    .weight(0.6f)
                                    .align(Alignment.CenterVertically),
                                color = Color.Black,
                            )
                            Text(
                                text = eventosConfirmados[index].startDate,
                                color = Color.Black,
                                modifier = Modifier
                                    .weight(0.4f)
                                    .align(Alignment.CenterVertically),
                                fontSize = 15.sp)
                            if (!isHomePage) {
                                Box {
                                    IconButton(onClick = { expanded = true }) {
                                        Icon(Icons.Default.MoreVert, contentDescription = "Más opciones", tint = Color.Black)
                                    }
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier.background(FondoTarjetaInception)
                                    ) {
                                        DropdownMenuItem(onClick = {
                                            expanded = false
                                            val arrayFecha = eventosConfirmados[index].startDate.split("/")
                                            val arrayHora = eventosConfirmados[index].horaComienzo.split(":")
                                            val startMillis: Long = Calendar.getInstance().run {
                                                set(arrayFecha[2].toInt(), arrayFecha[1].toInt(), arrayFecha[0].toInt(), arrayHora[0].toInt(), arrayHora[1].toInt())
                                                timeInMillis
                                            }
                                            val endMillis: Long = Calendar.getInstance().run {
                                                set(arrayFecha[2].toInt(), arrayFecha[1].toInt(), arrayFecha[0].toInt(), arrayHora[0].toInt() + 2, arrayHora[1].toInt())
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
                                        },
                                            text = {Text("Añadir al calendario", color = Color.Black)},
                                            leadingIcon = {Icon(
                                                Icons.Filled.DateRange,
                                                contentDescription = "Añadir al calendario",
                                                tint = Color.Black
                                            )},
                                            modifier = Modifier
                                                .background(Color.Transparent)
                                                .clip(RoundedCornerShape(10.dp))
                                                .padding(3.dp)
                                                .border(
                                                    1.dp,
                                                    BordeIndvCards,
                                                    RoundedCornerShape(10.dp)
                                                )
                                                .wrapContentSize())
                                        DropdownMenuItem(onClick = {
                                            expanded = false
                                            onEventoEscogido(eventosConfirmados[index])
                                        },
                                            text = {Text("Ver en el mapa", color = Color.Black)},
                                            leadingIcon = { Icon(
                                                    Icons.Filled.LocationOn,
                                                    contentDescription = "Ver en el mapa",
                                                    tint = Color.Black
                                                )},
                                            modifier = Modifier
                                                .background(Color.Transparent)
                                                .clip(RoundedCornerShape(10.dp))
                                                .padding(3.dp)
                                                .border(
                                                    1.dp,
                                                    BordeIndvCards,
                                                    RoundedCornerShape(10.dp)
                                                )
                                                .wrapContentSize())
                                        if (canEdit) {
                                            DropdownMenuItem(onClick = {
                                                expanded = false
                                                indexActual = index
                                            },
                                                text = {Text(text = "Eliminar evento", color = Color.Red)},
                                                leadingIcon = {Icon(
                                                    Icons.Filled.Delete,
                                                    contentDescription = "Eliminar",
                                                    tint = Color.Black
                                                )},
                                                modifier = Modifier
                                                    .background(Color.Transparent)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .padding(3.dp)
                                                    .border(
                                                        1.dp,
                                                        BordeIndvCards,
                                                        RoundedCornerShape(10.dp)
                                                    )
                                                    .wrapContentSize())
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        Text(text = "No hay eventos confirmados", color = Color.Black)
    }
    if (showDialog) {
        AlertDialog(
            containerColor = FondoApp,
            onDismissRequest = {
                showDialog = false
            },
            title = {
                Text(text = "¿Está seguro?", color = Color.Black)
            },
            text = {
                Text("¿Desea eliminar este evento?", color = Color.Black)
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BordeIndvCards
                    ),
                    onClick = {
                        showDialog = false
                        CoroutineScope(Dispatchers.IO).launch {
                            firestore.eliminaEvento(eventosConfirmados[indexActual])
                            onElementoEliminado(true)
                        }
                    }
                ) {
                    Text("Eliminar", color = Color.Black)
                }
            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BordeIndvCards
                    ),
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("No", color = Color.Black)
                }
            }
        )
    }
}

fun checkIfCanEditSitios(rol: String):Boolean{
    return rol == "Coordinador" || rol == "RR.II." || rol == "Logística"
}

fun checkIfCanManageEmails(rol: String):Boolean{
    return rol == "Coordinador" || rol == "RR.II."
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