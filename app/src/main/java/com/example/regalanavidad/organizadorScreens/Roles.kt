package com.example.regalanavidad.organizadorScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.regalanavidad.modelos.Usuario
import com.example.regalanavidad.sharedScreens.firestore
import com.example.regalanavidad.sharedScreens.usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

var listaUsuariosCambiados = mutableStateOf(emptyList<Usuario>())
var listaUsuarios = mutableStateOf(emptyList<Usuario>())

@Composable
fun RolesTabScreen(){
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var usuariosCargados by remember { mutableStateOf(false) }
    var guardarCambios by remember { mutableStateOf(false) }

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
        if(usuariosCargados){
            FloatingActionButton(
                onClick = {
                    guardarCambios = true
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(0.dp, 0.dp, 20.dp, 20.dp)) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar sitio")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RolesSubMenu(drawerState: DrawerState, scope: CoroutineScope, usuarioRegistrado: Usuario){
    val options = listOf("Secretaría", "Tesorería", "RR.II.", "Logística", "Imagen", "Voluntario")
    var expanded by remember { mutableStateOf(false) }
    var rolSeleccionado by remember { mutableStateOf(usuario.nombreRango) }

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
                        if(rolSeleccionado != usuarioRegistrado.nombreRango){
                            rolSeleccionado = selectionOption
                            usuarioRegistrado.nombreRango = selectionOption
                            listaUsuariosCambiados.value += usuarioRegistrado
                            //crear la funcion en el firestoremanager para actualizar los roles de los usuarios guardados
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
