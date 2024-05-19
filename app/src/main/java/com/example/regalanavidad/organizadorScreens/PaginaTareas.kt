package com.example.regalanavidad.organizadorScreens

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.regalanavidad.R
import com.example.regalanavidad.modelos.Tarea
import com.example.regalanavidad.sharedScreens.firestore
import com.example.regalanavidad.sharedScreens.tareasVM
import com.example.regalanavidad.sharedScreens.usuario
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

var listaTareasCambiadas = mutableStateOf(emptyList<Tarea>())
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TareasScreen(){
    val tareas = tareasVM.tareas.collectAsState()
    val listaTareas = tareas.value.filter { !it.completada && it.rol == usuario.nombreRango }
    var showTareaDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var guardarCambios by remember { mutableStateOf(false) }
    var descripcion by remember { mutableStateOf("") }
    var fechaEscogida by remember{mutableStateOf(LocalDate.now()) }
    val fechaFormateada by remember{ derivedStateOf { DateTimeFormatter.ofPattern("dd/MM/yyyy").format(fechaEscogida) } }
    val fechaDialogState = rememberMaterialDialogState()
    val scope = rememberCoroutineScope()
    val options = listOf("Secretaría", "Tesorería", "RR.II.", "Logística", "Imagen", "Voluntario, Coordinador")
    var expanded by remember { mutableStateOf(false) }
    var rolSeleccionado by remember { mutableStateOf(usuario.nombreRango) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(key1 = guardarCambios) {
        listaTareasCambiadas.value.forEach { tarea ->
            firestore.editaTarea(tarea)
        }
        tareasVM.cargarTareas()
        listaTareasCambiadas.value = emptyList()
        guardarCambios = false
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
                                            tareasVM.cargarTareas()
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

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (listaTareas.isNotEmpty()) {
            LazyColumn {
                items(listaTareas.size) { index ->
                    TareaCard(listaTareas[index])
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
        if(listaTareasCambiadas.value.isNotEmpty()){
            FloatingActionButton(
                onClick = {
                    guardarCambios = true
                    Toast.makeText(context, "Actualizando tareas...", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(0.dp, 0.dp, 14.dp, 80.dp)
                    .size(55.dp)
            ) {
                Icon(painter = painterResource(id = R.drawable.save), contentDescription = "Actualizar tareas")
            }
        }
        FloatingActionButton(
            onClick = {
                showTareaDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(0.dp, 0.dp, 14.dp, 14.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Agregar tarea")
        }
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
            .height(60.dp)
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column (
                Modifier
                    .weight(0.4f)
                    .padding(0.4.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = tarea.descripcion, fontSize = 14.sp)
            }
            Column (
                Modifier.weight(0.25f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = tarea.fechaLimite, fontSize = 14.sp)
            }
            Column (
                Modifier.weight(0.35f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TareaCompletadaSubMenu(drawerState = drawerState, scope = scope, tarea = tarea)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareaCompletadaSubMenu(drawerState: DrawerState, scope: CoroutineScope, tarea: Tarea){
    val options = listOf("Completada", "Pendiente")
    var expanded by remember { mutableStateOf(false) }
    var completada by remember { mutableStateOf(tarea.completada) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier.menuAnchor(),
            readOnly = true,
            value = completada.let { if (it) "Completada" else "Pendiente" },
            onValueChange = {},
            textStyle = TextStyle(fontSize = 13.sp),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption, fontSize = 14.sp) },
                    onClick = {
                        if(selectionOption != tarea.completada.let { if (it) "Completada" else "Pendiente"}){
                            completada = selectionOption.let { it == "Completada" }
                            tarea.completada = completada
                            listaTareasCambiadas.value += tarea
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