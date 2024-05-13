package com.example.regalanavidad.organizadorScreens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController

@Composable
fun PaginaContactosCentrosEducativos(navController: NavController) {
    val context = LocalContext.current
    var navegaCorreo by remember { mutableStateOf(false) }

    if(navegaCorreo){
        navController.navigate("Mail")
    }

    Column (
        Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row (
            Modifier
                .weight(0.4f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
            ) {
            Text(text = centroEducativoElegido.nombreCentro)
        }
        Row (
            Modifier
                .fillMaxWidth()
                .weight(0.6f)
        ) {
            Column(
                Modifier
                .fillMaxWidth()) {
                Row (
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            startActivity(
                                context,
                                Intent(
                                    Intent.ACTION_DIAL,
                                    Uri.parse("tel:${centroEducativoElegido.numeroCentro}")
                                ),
                                null
                            )
                        }
                        .wrapContentSize()
                ){
                    Column (Modifier.weight(0.8f)) {
                        Text(text = centroEducativoElegido.numeroCentro)
                    }
                    Column (Modifier.weight(0.2f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Call, "Llamar", Modifier.clickable {
                            startActivity(context, Intent(Intent.ACTION_CALL, Uri.parse("tel:${centroEducativoElegido.numeroCentro}")), null)
                        })
                    }
                }
                Row (
                    Modifier
                        .fillMaxWidth()
                        .wrapContentSize()
                        .clickable {
                            navegaCorreo = true
                        }) {
                    Column (Modifier.weight(0.8f)) {
                        Text(text = centroEducativoElegido.correoCentro, fontSize = 10.sp)
                    }
                    Column (Modifier.weight(0.2f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Email, "Enviar correo", Modifier.clickable {
                            navegaCorreo = true
                        })
                    }
                }
            }
        }
    }
}