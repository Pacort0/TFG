package com.example.regalanavidad.organizadorScreens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

var listaUsuariosCambiados = mutableStateOf(emptyList<Usuario>())
var listaUsuarios = mutableStateOf(emptyList<Usuario>())

@Composable
fun RolesTabScreen(navController: NavController){
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var usuariosCargados by remember { mutableStateOf(false) }
    var guardarCambios by remember { mutableStateOf(false) }
    var showAlertDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = guardarCambios) {
            listaUsuariosCambiados.value.forEach { usuario ->
                firestore.editaUsuario(usuario)
            }
            listaUsuariosCambiados.value = emptyList()
            guardarCambios = false
    }
    LaunchedEffect(key1 = Unit) {
        val usuarios = firestore.getUsers()
        listaUsuarios.value = usuarios
        usuariosCargados = true
    }
    Box {
        Column {
            Text("Asignar roles",
                Modifier
                    .fillMaxWidth()
                    .weight(0.2f)
                    .padding(4.dp), fontSize = 24.sp, textAlign = TextAlign.Center)
            if (!usuariosCargados){
                Text("Cargando usuarios...")
            } else {
                LazyColumn (
                    Modifier
                        .fillMaxSize()
                        .weight(0.8f)
                        .padding(8.dp)) {
                    items(listaUsuarios.value) { usuarioRegistrado ->
                        if (usuarioRegistrado.nombreRango != "Coordinador"){
                            Card (
                                Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                                    .height(50.dp)) {
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
                    .size(70.dp)) {
                Icon(painter = painterResource(id = R.drawable.save), contentDescription = "Guardar roles")
            }
        }
    }
    if(showAlertDialog){
        AlertDialog(
            onDismissRequest = {
                showAlertDialog = false
            },
            title = {
                Text(text = "Tiene cambios sin guardar")
            },
            text = {
                Text("Si sale de la página sin guardar los cambios, perderá la información modificada.\n¿Está seguro de querer continuar?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAlertDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("Sí, estoy seguro")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showAlertDialog = false
                    }
                ) {
                    Text("No")
                }
            }
        )
    }
    BackHandler {
        if(listaUsuariosCambiados.value.isNotEmpty()){
            showAlertDialog = true
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
