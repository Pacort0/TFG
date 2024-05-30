package com.example.regalanavidad.organizadorScreens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.regalanavidad.modelos.Usuario
import com.example.regalanavidad.sharedScreens.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.example.regalanavidad.R

private var listaUsuariosCambiados = mutableStateOf(emptyList<Usuario>())
private var listaUsuarios = mutableStateOf(emptyList<Usuario>())

@Composable
fun RolesTabScreen(navController: NavController){
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Voluntarios", "Roles")
    var showAlertDialog by remember { mutableStateOf(false) }
    var llamadaBackHandler by remember { mutableStateOf(false) }
    var indexActual by remember { mutableIntStateOf(0) }

    Column {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        if (listaUsuariosCambiados.value.isNotEmpty()) {
                            showAlertDialog = true
                            indexActual = index
                        } else {
                            selectedTabIndex = index
                        }
                    },
                    text = { Text(text = title) }
                )
            }
        }
        // Renderizar el contenido basado en la pestaña seleccionada
        when (selectedTabIndex) {
            0 -> TabRoles(true)
            1 -> TabRoles(false)
        }

        // Mostrar el AlertDialog si es necesario
        if (showAlertDialog) {
            AlertDialog(
                onDismissRequest = { showAlertDialog = false },
                title = { Text(text = "Tiene cambios sin guardar") },
                text = { Text("Perderá la información modificada.\n¿Está seguro de querer continuar?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showAlertDialog = false
                            listaUsuariosCambiados.value = emptyList()
                            listaUsuarios.value = emptyList()
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
            if (listaUsuariosCambiados.value.isNotEmpty()) {
                llamadaBackHandler = true
                showAlertDialog = true
            } else {
                navController.popBackStack()
            }
        }
    }
}

@Composable
fun TabRoles(voluntarios: Boolean){
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var usuariosCargados by remember { mutableStateOf(false) }
    var guardarCambios by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = guardarCambios) {
        usuariosCargados = false
        listaUsuariosCambiados.value.forEach { usuario ->
            firestore.editaUsuario(usuario)
        }
        listaUsuariosCambiados.value = emptyList()
        usuariosCargados = true
        guardarCambios = false
    }
    LaunchedEffect(key1 = Unit) {
        val usuarios = firestore.getUsuarios()
        listaUsuarios.value = usuarios
        usuariosCargados = true
    }

    Box (
        modifier = Modifier.fillMaxSize()
            .background(Color(246, 246, 244))
            .padding(start = 20.dp, end = 20.dp)
    ) {
        Column (
            Modifier.fillMaxSize()
        ) {
            Text("Asignar roles",
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(8.dp), fontSize = 24.sp, textAlign = TextAlign.Center)
            if (!usuariosCargados || listaUsuarios.value.isEmpty()){
                Column (
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = "Cargando usuarios...",
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            } else {
                val usuariosFiltrados by remember { mutableStateOf(listaUsuarios.value.filter { usuario ->
                    if (voluntarios) {
                        usuario.nombreRango == "Voluntario"
                    } else {
                        usuario.nombreRango != "Voluntario"
                    }
                }) }
                LazyColumn (
                    Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(8.dp)) {
                    if (usuariosFiltrados.isEmpty()){
                        item {
                            Text("Todos los usuarios son Voluntarios", Modifier.padding(8.dp))
                        }
                    } else {
                        items(usuariosFiltrados) { usuarioRegistrado ->
                            if (usuarioRegistrado.nombreRango != "Coordinador"){
                                Card (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, Color(216, 216, 207), CircleShape)
                                        .wrapContentHeight(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(238,238,234)
                                    )) {
                                    Row (horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                                        Column (Modifier.weight(0.5f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                            Text(text = usuarioRegistrado.nombre, fontSize = 18.sp, color = Color.Black)
                                        }
                                        Column (Modifier.weight(0.5f), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
                                            RolesSubMenu(drawerState, scope, usuarioRegistrado)
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
        if(usuariosCargados && listaUsuariosCambiados.value.isNotEmpty()){
            val context = LocalContext.current
            FloatingActionButton(
                onClick = {
                    guardarCambios = true
                    Toast.makeText(context, "Actualizando roles...", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(0.dp, 0.dp, 20.dp, 20.dp)
                    .height(40.dp)
                    .width(130.dp)
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(painterResource(id = R.drawable.save), contentDescription = "Guardar", Modifier.size(30.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(text = "Guardar")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RolesSubMenu(drawerState: DrawerState, scope: CoroutineScope, usuarioRegistrado: Usuario){
    val options = listOf("Tesorería", "RR.II.", "Logística", "Imagen", "Voluntario")
    var expanded by remember { mutableStateOf(false) }
    var rolSeleccionado by remember { mutableStateOf(usuarioRegistrado.nombreRango) }

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
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption, fontSize = 18.sp) },
                    onClick = {
                        if(selectionOption != usuarioRegistrado.nombreRango){
                            rolSeleccionado = selectionOption
                            usuarioRegistrado.nombreRango = selectionOption
                            listaUsuariosCambiados.value += usuarioRegistrado
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
