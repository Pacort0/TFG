package com.example.regalanavidad.organizadorScreens

import android.os.Build
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.regalanavidad.R
import com.example.regalanavidad.modelos.Tarea
import com.example.regalanavidad.sharedScreens.firestore
import com.example.regalanavidad.sharedScreens.usuario
import com.example.regalanavidad.ui.theme.BordeIndvCards
import com.example.regalanavidad.ui.theme.FondoApp
import com.example.regalanavidad.ui.theme.FondoIndvCards
import com.example.regalanavidad.ui.theme.FondoMenus
import com.example.regalanavidad.ui.theme.FondoTarjetaInception
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private var listaTareasCambiadas = mutableStateOf(emptyList<Tarea>())
private var primeraVez = mutableStateOf(true)
private var navigate = mutableStateOf(false)
private var listaTareas = mutableStateOf(emptyList<Tarea>())

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TareasScreen(navController: NavController) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Pendientes", "Completadas")
    var showAlertDialog by remember { mutableStateOf(false) }
    var llamadaBackHandler by remember { mutableStateOf(false) }
    var indexActual by remember { mutableIntStateOf(0) }
    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = FondoIndvCards,
            contentColor = Color.Black
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        if (listaTareasCambiadas.value.isNotEmpty()) {
                            showAlertDialog = true
                            indexActual = index
                        } else {
                            selectedTabIndex = index
                        }
                    },
                    text = { Text(text = title, fontSize = 16.sp) }
                )
            }
        }
        // Renderizar el contenido basado en la pestaña seleccionada
        when (selectedTabIndex) {
            0 -> TareasTabScreen(false) // Tareas pendientes
            1 -> TareasTabScreen(true)  // Tareas completadas
        }
        if (showAlertDialog) {
            AlertDialog(
                onDismissRequest = { showAlertDialog = false },
                title = { Text(text = "Tiene cambios sin guardar") },
                text = { Text("Perderá la información modificada.\n¿Está seguro de querer continuar?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showAlertDialog = false
                            listaTareasCambiadas.value = emptyList()
                            if (llamadaBackHandler) {
                                navController.popBackStack()
                            } else {
                                selectedTabIndex = indexActual
                            }
                        }
                    ) {
                        Text("Sí, estoy seguro")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showAlertDialog = false
                            llamadaBackHandler = false
                        }
                    ) {
                        Text("No")
                    }
                }
            )
        }

        // Manejo del botón de retroceso
        BackHandler {
            if (listaTareasCambiadas.value.isNotEmpty()) {
                llamadaBackHandler = true
                showAlertDialog = true
            } else {
                navController.popBackStack()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TareasTabScreen(completadas: Boolean){
    var listaTareasFiltradas by remember{ mutableStateOf(emptyList<Tarea>()) }
    var showTareaDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var guardarCambios by remember { mutableStateOf(true) }
    var descripcion by remember { mutableStateOf("") }
    var fechaEscogida by remember{mutableStateOf(LocalDate.now()) }
    val fechaFormateada by remember{ derivedStateOf { DateTimeFormatter.ofPattern("dd/MM/yyyy").format(fechaEscogida) } }
    val fechaDialogState = rememberMaterialDialogState()
    val scope = rememberCoroutineScope()
    val options = listOf("Tesorería", "RR.II.", "Logística", "Imagen", "Voluntario", "Coordinador")
    var expanded by remember { mutableStateOf(false) }
    var rolSeleccionado by remember { mutableStateOf(usuario.nombreRango) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var refresca by remember { mutableStateOf(false)}
    val pullRefreshState = rememberPullRefreshState(refreshing = guardarCambios, onRefresh = {
        guardarCambios = !guardarCambios
        refresca = !refresca
    })

    LaunchedEffect(key1 = guardarCambios) {
        if (listaTareasCambiadas.value.isNotEmpty() || !navigate.value) {
            if (!refresca){
                listaTareasCambiadas.value.forEach { tarea ->
                    firestore.editaTarea(tarea)
                }
            }
            listaTareas.value = firestore.getTareas()
            listaTareasFiltradas = filtraTareas(listaTareas.value, completadas)
            listaTareasCambiadas.value = emptyList()
            guardarCambios = false
            refresca = false
            if (primeraVez.value) {
                navigate.value = true
                primeraVez.value = false
            }
        } else {
            listaTareasFiltradas = filtraTareas(listaTareas.value, completadas)
            listaTareasCambiadas.value = emptyList()
            guardarCambios = false
            refresca = false
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .pullRefresh(pullRefreshState)
    ) {
        Column (
            Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Tareas",
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center),
                    )
                    IconButton(
                        onClick = { showTareaDialog = true },
                        modifier = Modifier
                            .size(55.dp)
                            .align(Alignment.CenterEnd)
                            .wrapContentSize()
                            .padding(end = 20.dp)
                            .alpha(completadas.let { if (it) 0f else 1f }))
                    {
                        Icon(
                            Icons.Filled.AddCircle,
                            "Agregar tarea",
                            Modifier.fillMaxSize()
                        )
                    }
                }
            }
            if (guardarCambios){
                Column (
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Cargando tareas...", fontSize = 18.sp)
                }
            } else {
                Column(
                    Modifier
                        .fillMaxWidth()
                ) {
                    if (listaTareasFiltradas.isNotEmpty() && !guardarCambios) {
                        LazyColumn {
                            items(listaTareasFiltradas.size) { index ->
                                TareaCard(listaTareasFiltradas[index])
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "No hay tareas pendientes",
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
        if(listaTareasCambiadas.value.isNotEmpty()){
            FloatingActionButton(
                containerColor = FondoMenus,
                onClick = {
                    guardarCambios = true
                    Toast.makeText(context, "Actualizando tareas...", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(20.dp, 0.dp, 0.dp, 20.dp)
                    .height(35.dp)
                    .width(130.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier.size(22.dp),
                        painter = painterResource(id = R.drawable.save),
                        contentDescription = "Actualizar tareas"
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Guardar",
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        PullRefreshIndicator(
            refreshing = guardarCambios,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
    if (showTareaDialog){
        Dialog(onDismissRequest = {showTareaDialog = false}) {
            Box(modifier = Modifier
                .width(350.dp)
                .height(320.dp)
                .padding(35.dp)
                .background(Color.LightGray)
                .clip(RoundedCornerShape(20.dp))) {
                Column(
                    Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if(usuario.nombreRango == "Coordinador"){
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                        ) {
                            TextField(
                                modifier = Modifier.menuAnchor(),
                                readOnly = true,
                                value = rolSeleccionado,
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
                                        text = { Text(selectionOption, fontSize = 18.sp) },
                                        onClick = {
                                            rolSeleccionado = selectionOption
                                            expanded = false
                                            scope.launch { drawerState.close() }
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                    )
                                }
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = usuario.nombreRango,
                            onValueChange = {},
                            readOnly = true,
                            label = {
                                Text(
                                    text = "Cargo de la tarea",
                                    fontSize = 14.sp
                                )
                            })
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = {
                            Text(
                                text = "Descripción de la tarea",
                                fontSize = 13.sp
                            )
                        })
                    Spacer(modifier = Modifier.height(8.dp))
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Fecha límite de la tarea",
                            fontSize = 13.sp,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Text(
                            text = fechaFormateada,
                            modifier = Modifier.clickable { fechaDialogState.show() }
                        )
                    }
                    Row(
                        Modifier
                            .weight(0.2f),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Column(Modifier.weight(0.36f)) {}
                        Column(
                            Modifier
                                .weight(0.32f)
                                .clickable {
                                    showTareaDialog = false
                                }) {
                            Text(text = "CANCELAR", fontSize = 13.sp, color = Color.Magenta, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Column(
                            Modifier
                                .weight(0.32f)
                                .clickable {
                                    if (descripcion.isNotEmpty()) {
                                        val tarea =
                                            Tarea(
                                                rol = rolSeleccionado,
                                                descripcion = descripcion,
                                                fechaLimite = fechaFormateada,
                                                completada = false
                                            )
                                        showTareaDialog = false
                                        scope.launch(Dispatchers.IO) {
                                            firestore.insertaTarea(tarea)
                                            guardarCambios = true
                                        }
                                    } else {
                                        Toast
                                            .makeText(
                                                context,
                                                "Por favor, llena todos los campos",
                                                Toast.LENGTH_SHORT
                                            )
                                            .show()
                                    }
                                }) {
                            Text(text = "GUARDAR", fontSize = 13.sp, color = Color.Magenta, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
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
}
@Composable
fun TareaCard(tarea: Tarea){
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    Card (
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clip(CircleShape)
            .border(1.dp, BordeIndvCards, CircleShape)
            .heightIn(min = 60.dp),
        colors = CardDefaults.cardColors(
            containerColor = FondoIndvCards
        )
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column (
                Modifier
                    .weight(0.45f)
                    .padding(0.4.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = tarea.descripcion, fontSize = 16.sp)
            }
            Column (
                Modifier.weight(0.55f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TareaCompletadaSubMenu(drawerState = drawerState, scope = scope, tarea = tarea, estadoPrevio = tarea.completada)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareaCompletadaSubMenu(drawerState: DrawerState, scope: CoroutineScope, tarea: Tarea, estadoPrevio: Boolean){
    val options = listOf("Completada", "Pendiente")
    var expanded by remember { mutableStateOf(false) }
    var completada by remember { mutableStateOf(tarea.completada) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .padding(5.dp)
            .clip(CircleShape)
            .border(1.dp, BordeIndvCards, CircleShape)
            .background(FondoTarjetaInception)
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = completada.let { if (it) "Completada" else "Pendiente" },
            onValueChange = {},
            textStyle = TextStyle(fontSize = 16.sp),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
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
            modifier = Modifier.background(FondoTarjetaInception)
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption, fontSize = 14.sp) },
                    onClick = {
                        if(selectionOption != tarea.completada.let { if (it) "Completada" else "Pendiente"}){
                            completada = selectionOption == "Completada"
                            tarea.completada = completada

                            val index = listaTareasCambiadas.value.indexOf(tarea)

                            if (index < 0 && tarea.completada != estadoPrevio) {
                                listaTareasCambiadas.value += tarea
                            } else if (index >= 0 && listaTareasCambiadas.value[index].completada == tarea.completada){
                                listaTareasCambiadas.value = listaTareasCambiadas.value.filterNot { it.id == tarea.id }
                            } else {
                                listaTareasCambiadas.value[index].completada = tarea.completada
                            }
                        }
                        expanded = false
                        scope.launch { drawerState.close() }
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    modifier = Modifier
                        .background(Color.Transparent)
                        .clip(RoundedCornerShape(10.dp))
                        .padding(3.dp)
                        .border(1.dp, BordeIndvCards, RoundedCornerShape(10.dp))
                        .wrapContentSize()
                )
            }
        }
    }
}

fun filtraTareas(listaTareas: List<Tarea>, completadas: Boolean): List<Tarea> {
    val listaTareasFiltradas =
        if (completadas) {
            if (usuario.nombreRango != "Coordinador") {
                listaTareas.filter { it.completada && it.rol == usuario.nombreRango }
            } else {
                listaTareas.filter { it.completada }
            }
        } else {
            if (usuario.nombreRango != "Coordinador") {
                listaTareas.filter { !it.completada && it.rol == usuario.nombreRango }
            } else {
                listaTareas.filter { !it.completada }
            }
        }
    return listaTareasFiltradas
}