package com.example.regalanavidad.organizadorScreens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.regalanavidad.modelos.CentroEducativo

@Composable
fun MailScreen(navController: NavController){
    var correoContacto by remember { mutableStateOf(centroEducativoElegido.correoCentro) }
    var asuntoCorreo by remember { mutableStateOf("") }
    var mensajeCorreo by remember { mutableStateOf("") }
    val contexto = LocalContext.current
    var showAlertDialog by remember { mutableStateOf(false) }

    Column (
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            label = { Text(text = "Correo") },
            value = correoContacto,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            onValueChange = { correoContacto = it }
        )
        TextField(
            label = { Text(text = "Asunto") },
            value = asuntoCorreo,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            onValueChange = { asuntoCorreo = it }
        )
        TextField(
            label = { Text(text = "Mensaje") },
            value = mensajeCorreo,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            onValueChange = { mensajeCorreo = it }
        )
        Button(onClick = {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "message/rfc822"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(correoContacto))
            intent.putExtra(Intent.EXTRA_SUBJECT, asuntoCorreo)
            intent.putExtra(Intent.EXTRA_TEXT, mensajeCorreo)

            try {
                contexto.startActivity(Intent.createChooser(intent, "Enviar correo"))
                correoContacto = ""
                asuntoCorreo = ""
                mensajeCorreo = ""
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(contexto, "No hay aplicaciones de correo instaladas", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = "Enviar")
        }
    }
    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            title = { Text(text = "Tiene cambios sin guardar") },
            text = { Text("Perderá la información modificada.\n¿Está seguro de querer continuar?") },
            confirmButton = {
                Button(
                    onClick = {
                        showAlertDialog = false
                        navController.popBackStack()
                        centroEducativoElegido = CentroEducativo()
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
        if (correoContacto != "" || asuntoCorreo != "" || mensajeCorreo != "") {
            showAlertDialog = true
        }
        else {
            navController.popBackStack()
            centroEducativoElegido = CentroEducativo()
        }
    }
}