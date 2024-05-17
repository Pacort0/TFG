package com.example.regalanavidad.organizadorScreens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ExcelCoordinador(navController: NavController){
    var navegaSitiosRecogida by remember{ mutableStateOf(false) }
    var navegaGastos by remember{ mutableStateOf(false)}

    if (navegaSitiosRecogida){
        navController.navigate("SheetCentrosEducativos")
    }
    if (navegaGastos){
        navController.navigate("SheetGastos")
    }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(10.dp)
    ) {
        Row (
            Modifier
                .weight(0.5f)
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
            ) {
            Card (
                Modifier.fillMaxSize().clickable {
                    navegaSitiosRecogida = true
                }
            ) {

            }
        }
        Row (
            Modifier
                .weight(0.5f)
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card (
                Modifier.fillMaxSize().clickable {
                    navegaGastos = true
                }
            ) {

            }
        }
    }
}