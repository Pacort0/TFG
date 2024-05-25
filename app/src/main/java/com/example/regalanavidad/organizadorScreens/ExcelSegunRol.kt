package com.example.regalanavidad.organizadorScreens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.regalanavidad.R

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ExcelCoordinador(navController: NavController){
    val textoCentros = "Centros Educativos"
    val textoGastos = "Gastos"
    val textoRecaudaciones = "Productos Recaudados"
    val nombreRutaCentros = "SheetCentrosEducativos"
    val nombreRutaGastos = "SheetGastos"
    val nombreRutaRecaudaciones = "SheetRecaudaciones"

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(10.dp)
    ) {
        Row (
            Modifier
                .weight(0.33f)
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
            ) {
            CartaExcel(navController, textoCentros, nombreRutaCentros)
        }
        Row (
            Modifier
                .weight(0.33f)
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CartaExcel(navController, textoGastos, nombreRutaGastos)
        }
        Row (
            Modifier
                .weight(0.33f)
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CartaExcel(navController, textoRecaudaciones, nombreRutaRecaudaciones)
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
                .background(Color(184, 243, 175)),
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
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp,
                    modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp)
                )
            }
        }
    }
}