package com.example.regalanavidad.organizadorScreens

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.regalanavidad.sharedScreens.NoInternetScreen
import com.example.regalanavidad.sharedScreens.hayInternet
import com.example.regalanavidad.sharedScreens.usuario

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ExcelScreen(navController: NavController, onMapaCambiado: (Boolean) -> Unit){
    val context = LocalContext.current
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var hayInternet by remember { mutableStateOf(hayInternet(connectivityManager)) }
    var mostrarTodo by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit, key2 = hayInternet) {
        hayInternet = hayInternet(connectivityManager)
        mostrarTodo = hayInternet
    }

    if(!mostrarTodo) {
        NoInternetScreen(
            onRetry = {
                hayInternet = true
            }
        )
    } else {
        when(usuario.nombreRango){
            "Coordinador" -> {
                ExcelCoordinador(navController)
            }
            "Secretaría" -> {
                Text(text = "Hola secretari@")
            }
            "Tesorería" -> {
                Text(text = "Hola tesorer@")
            }
            "RR.II." -> {
                PaginaSheetCentrosEducativos(navController, onMapaCambiado)
            }
        }
    }
}