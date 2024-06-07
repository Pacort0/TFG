package com.example.regalanavidad.organizadorScreens

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.example.regalanavidad.dal.getCentrosDataFromGoogleSheet
import com.example.regalanavidad.dal.updateCentrosDataInGoogleSheet
import com.example.regalanavidad.R
import com.example.regalanavidad.modelos.CentroEducativo
import com.example.regalanavidad.modelos.CentroEducativoRequest
import com.example.regalanavidad.modelos.CentroEducativoResponse
import com.example.regalanavidad.sharedScreens.NoInternetScreen
import com.example.regalanavidad.sharedScreens.PantallaCarga
import com.example.regalanavidad.sharedScreens.hayInternet
import com.example.regalanavidad.ui.theme.ColorLogo
import com.example.regalanavidad.ui.theme.FondoApp
import com.example.regalanavidad.ui.theme.FondoIndvCards
import com.example.regalanavidad.ui.theme.FondoMenus
import com.example.regalanavidad.ui.theme.FondoTarjetaInception
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private var listaCentrosEducativos = mutableStateOf(emptyList<CentroEducativo>())
const val infoCentrosSheetId = "1RtpW4liafATG-CW-tFozrFZzM-8tzg9e_KIj-9DT4gA"
private var listaCentrosCambiados = mutableStateOf(emptyList<CentroEducativoRequest>())
var centroEducativoElegido = CentroEducativo()
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PaginaSheetCentrosEducativos(navController: NavController, onMapaCambiado: (Boolean) -> Unit) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var expanded by remember { mutableStateOf(false) }
    val opcionesDistritos = listOf("Sevilla Este", "Aljarafe", "Montequinto", "Casco Antiguo", "Nervión-Porvenir", "Triana", "Heliópolis", "Facultades US")
    var distritoSeleccionado by remember { mutableStateOf(opcionesDistritos[3]) }
    var centrosLoading by remember { mutableStateOf(true) }
    var showAlertDialog by remember { mutableStateOf(false)}
    var opcionSeleccionada by remember {mutableStateOf("")}
    var llamadaBackHandler by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val pullRefreshState = rememberPullRefreshState(refreshing = centrosLoading, onRefresh = {centrosLoading = !centrosLoading})
    var navegaCorreo by remember { mutableStateOf(false) }
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var hayInternet by remember { mutableStateOf(hayInternet(connectivityManager)) }
    var mostrarTodo by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = centrosLoading) {
        onMapaCambiado(true)
        hayInternet = hayInternet(connectivityManager)
        if (hayInternet) {
            listaCentrosEducativos.value = getCentrosFromDistrito(distritoSeleccionado = distritoSeleccionado)
        }
        mostrarTodo = hayInternet
        centrosLoading = false
    }

    if(centrosLoading){
        PantallaCarga(textoCargando = "Cargando centros...")
    } else if (!mostrarTodo){
        NoInternetScreen(
            onRetry = {
                centrosLoading = true
                listaCentrosCambiados.value = emptyList()
            }
        )
    } else {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .padding(8.dp)
            .pullRefresh(pullRefreshState)
        ) {
            Column {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Centros Educativos",
                        fontSize = 24.sp,
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row (
                    Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .weight(0.55f)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(FondoTarjetaInception)
                        ) {
                            TextField(
                                modifier = Modifier
                                    .menuAnchor()
                                    .clip(CircleShape)
                                    .border(0.dp, Color.Black, CircleShape),
                                readOnly = true,
                                value = distritoSeleccionado,
                                textStyle = TextStyle(fontSize = 15.sp, textAlign = TextAlign.Center, color = Color.Black),
                                onValueChange = {},
                                trailingIcon = { TrailingIconMio(expanded = expanded) },
                                colors = TextFieldDefaults.colors(
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    focusedContainerColor = FondoTarjetaInception,
                                    unfocusedContainerColor = FondoTarjetaInception
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier
                                    .background(FondoTarjetaInception)
                            ) {
                                opcionesDistritos.forEach { selectionOption ->
                                    DropdownMenuItem(
                                        text = { Text(selectionOption, fontSize = 18.sp, color = Color.Black) },
                                        onClick = {
                                            if (listaCentrosCambiados.value.isNotEmpty()) {
                                                showAlertDialog = true
                                                opcionSeleccionada = selectionOption
                                            } else {
                                                distritoSeleccionado = selectionOption
                                                expanded = false
                                                centrosLoading = true
                                            }
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                        modifier = Modifier
                                            .background(FondoTarjetaInception)
                                            .padding(5.dp)
                                            .border(1.dp, ColorLogo, CircleShape),
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(3.dp))
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .weight(0.45f),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (listaCentrosCambiados.value.isNotEmpty()) {
                            ElevatedButton(
                                onClick = {
                                    hayInternet = hayInternet(connectivityManager)
                                    if (hayInternet){
                                        Toast.makeText(context, "Actualizando centros...", Toast.LENGTH_SHORT)
                                            .show()
                                        scope.launch(Dispatchers.IO) {
                                            updateCentrosDataInGoogleSheet(
                                                infoCentrosSheetId,
                                                cambiaNombreDistrito(distritoSeleccionado),
                                                listaCentrosCambiados.value
                                            )
                                            listaCentrosCambiados.value = emptyList()
                                            centrosLoading = true }
                                    } else {
                                        mostrarTodo = false
                                        listaCentrosCambiados.value = emptyList()
                                    }
                                    },
                                modifier = Modifier.width(160.dp),
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = FondoTarjetaInception
                                ),
                                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 4.dp, pressedElevation = 8.dp))
                            {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        painterResource(id = R.drawable.save),
                                        contentDescription = "Guardar cambios",
                                        Modifier.size(32.dp),
                                        tint = Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "Guardar", fontSize = 18.sp, color = Color.Black)
                                }
                            }
                        }
                    }
                }
                if(listaCentrosEducativos.value.isNotEmpty() && !centrosLoading){
                    LazyColumn {
                        items(listaCentrosEducativos.value.size) { index ->
                            var isExpanded by remember { mutableStateOf(false) }  // Añadir estado para controlar la expansión
                            Card (
                                modifier = Modifier
                                    .padding(top = 5.dp, start = 5.dp, end = 5.dp, bottom = 0.dp)
                                    .fillParentMaxWidth()
                                    .heightIn(min = 80.dp)
                                    .wrapContentHeight()
                                    .clip(RoundedCornerShape(20.dp))
                                    .border(0.dp, Color.Transparent, RectangleShape)
                                    .animateContentSize(
                                        animationSpec = tween(
                                            durationMillis = 300,
                                            easing = LinearOutSlowInEasing
                                        )
                                    ),
                                colors = CardDefaults.cardColors(
                                    containerColor = FondoIndvCards
                                )
                            ) {
                                Row (
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { isExpanded = !isExpanded }
                                        .padding(15.dp)){
                                    Column(
                                        Modifier
                                            .weight(0.1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center)
                                    {
                                        Icon(imageVector = isExpanded.let { if (!isExpanded) {
                                            Icons.Default.KeyboardArrowDown
                                        } else {
                                            Icons.Default.KeyboardArrowUp
                                        } }, contentDescription = "Contraer", Modifier.size(30.dp), tint = Color.Black)
                                    }
                                    Column (
                                        Modifier
                                            .weight(0.45f)
                                            .wrapContentHeight(),
                                        horizontalAlignment = Alignment.Start,
                                        verticalArrangement = Arrangement.Center) {
                                        Text(
                                            text = listaCentrosEducativos.value[index].nombreCentro,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    }
                                    Column (
                                        Modifier
                                            .weight(0.45f)
                                            .wrapContentHeight(),
                                        horizontalAlignment = Alignment.End,
                                        verticalArrangement = Arrangement.Center) {
                                        EstadosSubMenu(drawerState, scope, listaCentrosEducativos.value[index])
                                    }
                                }
                                if (isExpanded){
                                    Column (
                                        Modifier
                                            .padding(10.dp)
                                            .wrapContentHeight()
                                    ) {
                                        Row (
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            var esTextField by remember { mutableStateOf(false) }
                                            val tareaOriginal = listaCentrosEducativos.value[index].tareaCentro
                                            var nuevaTarea by remember { mutableStateOf("") }
                                            var textFieldHasFocus by remember { mutableStateOf(false) }

                                            fun saveTarea() {
                                                esTextField = false
                                                val centro = listaCentrosEducativos.value[index]
                                                centro.tareaCentro = nuevaTarea
                                                if (listaCentrosCambiados.value.find { it.nombreCentro == centro.nombreCentro } == null) {
                                                    val centroRequest = centro.toCentroEducativoRequest()
                                                    centroRequest.tareaCentro = nuevaTarea
                                                    listaCentrosCambiados.value += centroRequest
                                                } else {
                                                    val indice = listaCentrosCambiados.value.indexOfFirst { it.nombreCentro == centro.nombreCentro }
                                                    val newList = listaCentrosCambiados.value.toMutableList()
                                                    newList[indice].tareaCentro = nuevaTarea
                                                    listaCentrosCambiados.value = newList
                                                }
                                            }

                                            if (esTextField) {
                                                DisposableEffect(Unit) {
                                                    onDispose {
                                                        if (!textFieldHasFocus) {
                                                            esTextField = false
                                                        }
                                                    }
                                                }

                                                TextField(
                                                    placeholder = { Text(text = "Previa: $tareaOriginal", color = Color.Black) },
                                                    value = nuevaTarea,
                                                    textStyle = TextStyle(color = Color.Black),
                                                    onValueChange = { nuevaTarea = it },
                                                    label = { Text("Tarea", color = Color.Black) },
                                                    modifier = Modifier
                                                        .weight(0.75f)
                                                        .padding(4.dp)
                                                        .clip(CircleShape)
                                                        .onFocusChanged { focusState ->
                                                            textFieldHasFocus = focusState.isFocused
                                                        },
                                                    colors = TextFieldDefaults.colors(
                                                        unfocusedIndicatorColor = Color.Transparent,
                                                        focusedIndicatorColor = Color.Transparent,
                                                        focusedContainerColor = FondoTarjetaInception,
                                                        unfocusedContainerColor = FondoTarjetaInception,
                                                        cursorColor = Color.Black
                                                    ),
                                                    keyboardActions = KeyboardActions(
                                                        onDone = {
                                                            saveTarea()
                                                        }
                                                    ),
                                                    keyboardOptions = KeyboardOptions(
                                                        imeAction = ImeAction.Done
                                                    )
                                                )
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Icon(
                                                    Icons.Filled.Done,
                                                    contentDescription = "Confirmar",
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .weight(0.125f)
                                                        .clickable {
                                                            saveTarea()
                                                        },
                                                    tint = Color.Black
                                                )
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Icon(
                                                    Icons.Filled.Clear,
                                                    contentDescription = "Cancelar",
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .weight(0.125f)
                                                        .clickable { esTextField = false },
                                                    tint = Color.Black
                                                )
                                            } else {
                                                Text(
                                                    text = "Tarea: ${listaCentrosEducativos.value[index].tareaCentro}",
                                                    fontSize = 18.sp,
                                                    textAlign = TextAlign.Start,
                                                    color = Color.Black
                                                )
                                                Spacer(modifier = Modifier.width(5.dp))
                                                Icon(
                                                    Icons.Filled.Edit,
                                                    contentDescription = "Editar tarea",
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .clickable { esTextField = true },
                                                    tint = Color.Black
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(15.dp))
                                        Row (
                                            Modifier.wrapContentSize()
                                        ) {
                                            Box (
                                                modifier = Modifier
                                                    .weight(0.45f)
                                                    .fillMaxSize()
                                                    .padding(start = 10.dp)
                                            ){
                                                Row (
                                                    Modifier
                                                        .fillMaxSize()
                                                        .clickable {
                                                            startActivity(
                                                                context,
                                                                Intent(
                                                                    Intent.ACTION_DIAL,
                                                                    Uri.parse("tel:${listaCentrosEducativos.value[index].numeroCentro}")
                                                                ),
                                                                null
                                                            )
                                                        },
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(
                                                        Icons.Filled.Call,
                                                        "Llamar",
                                                        tint = Color.Black
                                                    )
                                                    Spacer(modifier = Modifier.width(5.dp))
                                                    Text(text = "Llamar", color = Color.Black)
                                                }
                                            }
                                            Box (
                                                modifier = Modifier
                                                    .weight(0.1f)
                                                    .fillMaxSize()
                                            ){
                                                Row (
                                                    Modifier.fillMaxSize(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    Text(text = "|", fontSize = 28.sp, color = Color.Black)
                                                }
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .weight(0.45f)
                                                    .fillMaxSize()
                                                    .padding(end = 15.dp)
                                            ){
                                                Row (
                                                    Modifier
                                                        .fillMaxSize()
                                                        .clickable { navegaCorreo = true },
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ){
                                                    Icon(
                                                        Icons.Filled.Email,
                                                        "Correo",
                                                        tint = Color.Black
                                                    )
                                                    Spacer(modifier = Modifier.width(5.dp))
                                                    Text(text = "Redactar correo", color = Color.Black)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(navegaCorreo){
                        navController.navigate("Mail")
                    }
                } else {
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.googlesheetslogo),
                            contentDescription = "GoogleSheetsLogo",
                            modifier = Modifier.size(60.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Cargando centros...",
                            modifier = Modifier.padding(top = 8.dp),
                            color = Color.Black
                        )
                    }
                }
            }
            if(showAlertDialog){
                AlertDialog(
                    containerColor = FondoApp,
                    onDismissRequest = {
                        showAlertDialog = false
                    },
                    title = {
                        Text(text = "Tiene cambios sin guardar", color = Color.Black)
                    },
                    text = {
                        Text("Si sale de la página sin guardar los cambios, perderá la información modificada.\n¿Está seguro de querer continuar?",
                            color = Color.Black)
                    },
                    confirmButton = {
                        if(llamadaBackHandler){
                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ColorLogo
                                ),
                                onClick = {
                                    showAlertDialog = false
                                    navController.popBackStack()
                                }
                            ){
                                Text("Sí, estoy seguro", color = Color.Black)
                            }
                        }
                        else {
                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ColorLogo
                                ),
                                onClick = {
                                    showAlertDialog = false
                                    distritoSeleccionado = opcionSeleccionada
                                    expanded = false
                                    centrosLoading = true
                                    listaCentrosCambiados.value = emptyList()
                                }
                            ) {
                                Text("Sí, estoy seguro", color = Color.Black)
                            }
                        }
                    },
                    dismissButton = {
                        if (llamadaBackHandler){
                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ColorLogo
                                ),
                                onClick = {
                                    showAlertDialog = false
                                    llamadaBackHandler = false
                                }
                            ){
                                Text(text = "No", color = Color.Black)
                            }
                        } else {
                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ColorLogo
                                ),
                                onClick = {
                                    showAlertDialog = false
                                }
                            ) {
                                Text("No", color = Color.Black)
                            }
                        }
                    }
                )
            }
            BackHandler {
                if(listaCentrosCambiados.value.isNotEmpty()){
                    llamadaBackHandler = true
                    showAlertDialog = true
                } else {
                    navController.popBackStack()
                    listaCentrosCambiados.value = emptyList()
                }
            }
            PullRefreshIndicator(
                refreshing = centrosLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
fun cambiaNombreDistrito(sheetName: String):String{
    var nuevoSheetName = ""
    when (sheetName){
        "Sevilla Este" -> {
            nuevoSheetName = "SevillaEste"
        }
        "Aljarafe" -> {
            nuevoSheetName = "Aljarafe"
        }
        "Montequinto" -> {
            nuevoSheetName =  "Montequinto"
        }
        "Casco Antiguo" -> {
            nuevoSheetName =  "CascoAntiguo"
        }
        "Nervión-Porvenir" -> {
            nuevoSheetName =  "NervionPorvenir"
        }
        "Triana" -> {
            nuevoSheetName =  "Triana"
        }
        "Heliópolis" -> {
            nuevoSheetName =  "Heliopolis"
        }
        "Facultades US" -> {
            nuevoSheetName = "FacultadesUS"
        }
    }
    return nuevoSheetName
}

fun cambiaColorEstado(estado: String): Color {
    return when (estado) {
        "No Iniciada" -> Color(0XFFe8eaed)
        "En curso" -> Color(0XFFc6dbe1)
        "En revision" -> Color(0xFFfeffab)
        "Completada" -> Color(0xFFb6ffab)
        "Cancelada" -> Color(0xFFffabab)
        else -> Color(0, 0, 0)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadosSubMenu(drawerState: DrawerState, scope: CoroutineScope, centroEducativo: CentroEducativo){
    val opcionesEstados = listOf("No Iniciada", "En curso", "En revision", "Completada", "Cancelada")
    var expanded by remember { mutableStateOf(false) }
    var nuevoEstado by remember { mutableStateOf(centroEducativo.estadoCentro) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .clip(CircleShape)
            .border(0.dp, Color.Transparent, CircleShape)
            .background(cambiaColorEstado(nuevoEstado))
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = nuevoEstado,
            textStyle = TextStyle(fontSize = 15.sp, textAlign = TextAlign.Center, color = Color.Black),
            onValueChange = {},
            trailingIcon = { TrailingIconMio(expanded = expanded) },
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedContainerColor = cambiaColorEstado(nuevoEstado),
                unfocusedContainerColor = cambiaColorEstado(nuevoEstado)
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.Transparent)
        ) {
            opcionesEstados.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption, fontSize = 18.sp, color = Color.Black) },
                    onClick = {
                        if(selectionOption != centroEducativo.estadoCentro){
                            nuevoEstado = selectionOption
                            centroEducativo.estadoCentro = selectionOption

                            // Buscar si ya existe un registro para este centro
                            val index = listaCentrosCambiados.value.indexOfFirst { it.nombreCentro == centroEducativo.nombreCentro }

                            if (index != -1) {
                                // Si existe, sobrescribirlo
                                val newList = listaCentrosCambiados.value.toMutableList()
                                newList[index] = centroEducativo.toCentroEducativoRequest()
                                listaCentrosCambiados.value = newList
                            } else {
                                // Si no existe, añadirlo a la lista
                                listaCentrosCambiados.value += centroEducativo.toCentroEducativoRequest()
                            }
                        }
                        expanded = false
                        scope.launch { drawerState.close() }
                        Log.d("Centros", listaCentrosCambiados.value.toString())
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    modifier = Modifier
                        .background(cambiaColorEstado(selectionOption))
                        .padding(3.dp),
                )
            }
        }
    }
}

suspend fun getCentrosFromDistrito(distritoSeleccionado:String):List<CentroEducativo>{
    var centroResponse = CentroEducativoResponse(emptyList())
    when(distritoSeleccionado){
        "Sevilla Este" -> {
            centroResponse = getCentrosDataFromGoogleSheet(infoCentrosSheetId, "SevillaEste")
        }
        "Aljarafe" -> {
            centroResponse = getCentrosDataFromGoogleSheet(infoCentrosSheetId, "Aljarafe")
        }
        "Montequinto" -> {
            centroResponse = getCentrosDataFromGoogleSheet(infoCentrosSheetId, "Montequinto")
        }
        "Casco Antiguo" -> {
            centroResponse = getCentrosDataFromGoogleSheet(infoCentrosSheetId, "CascoAntiguo")
        }
        "Nervión-Porvenir" -> {
            centroResponse = getCentrosDataFromGoogleSheet(infoCentrosSheetId, "NervionPorvenir")
        }
        "Triana" -> {
            centroResponse = getCentrosDataFromGoogleSheet(infoCentrosSheetId, "Triana")
        }
        "Heliópolis" -> {
            centroResponse = getCentrosDataFromGoogleSheet(infoCentrosSheetId, "Heliopolis")
        }
        "Facultades US" -> {
            centroResponse = getCentrosDataFromGoogleSheet(infoCentrosSheetId, "FacultadesUS")
        }
    }
    Log.d("Centros", centroResponse.toString())
    return centroResponse.centros
}