package com.example.regalanavidad.organizadorScreens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.regalanavidad.sharedScreens.usuario

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ExcelScreen(navController: NavController, onMapaCambiado: (Boolean) -> Unit){
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