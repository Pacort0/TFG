package com.example.regalanavidad.organizadorScreens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.regalanavidad.modelos.Usuario
import com.example.regalanavidad.sharedScreens.firestore
@Composable
fun RolesTabScreen(){
    var listaUsuarios by remember { mutableStateOf(emptyList<Usuario>()) }
    var usuariosCargados by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit) {
        val usuarios = firestore.getUsers()
        listaUsuarios = usuarios.toMutableList()
        usuariosCargados = true
    }
    Column {
        Text("Asignar roles")
        if (!usuariosCargados){
            Text("Cargando usuarios...")
        } else {
            LazyColumn {
                items(listaUsuarios) { usuario ->
                    Text(usuario.nombre)
                }
            }
        }
    }
}
