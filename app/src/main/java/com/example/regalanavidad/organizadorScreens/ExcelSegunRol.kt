package com.example.regalanavidad.organizadorScreens

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.regalanavidad.R
import com.example.regalanavidad.sharedScreens.NoInternetScreen
import com.example.regalanavidad.sharedScreens.hayInternet
import com.example.regalanavidad.sharedScreens.usuario
import com.example.regalanavidad.ui.theme.FondoApp
import com.example.regalanavidad.ui.theme.FondoMenus

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
        if (usuario.nombreRango == "Coordinador" ||
            usuario.nombreRango == "RR.II." ||
            usuario.nombreRango == "Logística" ||
            usuario.nombreRango == "Tesorería") {
            ExcelRol(navController)
            onMapaCambiado(false)
        } else {
            navController.navigate("SheetGastos")

        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ExcelRol(navController: NavController) {
    val textoCentros = "Centros Educativos"
    val textoGastos = "Gastos"
    val textoRecaudaciones = "Productos Recaudados"
    val nombreRutaCentros = "SheetCentrosEducativos"
    val nombreRutaGastos = "SheetGastos"
    val nombreRutaRecaudaciones = "SheetRecaudaciones"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().background(FondoApp)
    ) {
        if (usuario.nombreRango != "Coordinador") {
            Spacer(modifier = Modifier.weight(0.16f))
        }
        if (usuario.nombreRango == "Coordinador" || usuario.nombreRango == "RR.II." || usuario.nombreRango == "Logística") {
            Row(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CartaExcel(navController, textoCentros, nombreRutaCentros)
            }
        }

        Row(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CartaExcel(navController, textoGastos, nombreRutaGastos)
        }

        if (usuario.nombreRango == "Coordinador" || usuario.nombreRango == "Tesorería") {
            Row(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CartaExcel(navController, textoRecaudaciones, nombreRutaRecaudaciones)
            }
        }
        if (usuario.nombreRango != "Coordinador") {
            Spacer(modifier = Modifier.weight(0.16f))
        }
    }
}


@Composable
private fun CartaExcel(
    navController: NavController,
    textoCarta: String,
    nombreRuta: String
) {
    Card(
        Modifier
            .fillMaxSize()
            .clickable {
                navController.navigate(nombreRuta)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoMenus),
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
                    painter = painterResource(id = R.drawable.googlesheetslogo),
                    contentDescription = "Logo",
                    contentScale = ContentScale.Crop,
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
                    text = textoCarta,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp,
                    modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp)
                )
            }
        }
    }
}