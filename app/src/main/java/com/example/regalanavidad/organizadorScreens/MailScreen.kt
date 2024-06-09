package com.example.regalanavidad.organizadorScreens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.regalanavidad.R
import com.example.regalanavidad.modelos.CentroEducativo
import com.example.regalanavidad.ui.theme.ColorLogo
import com.example.regalanavidad.ui.theme.FondoApp
import com.example.regalanavidad.ui.theme.FondoIndvCards
import com.example.regalanavidad.ui.theme.FondoTarjetaInception

@Composable
fun MailScreen(navController: NavController){
    var correoContacto by remember { mutableStateOf(centroEducativoElegido.correoCentro) }
    var asuntoCorreo by remember { mutableStateOf("") }
    var mensajeCorreo by remember { mutableStateOf("") }
    val contexto = LocalContext.current
    var showAlertDialog by remember { mutableStateOf(false) }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .weight(0.1f)
        ) {
            Column (
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.2f),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                Image(painter = painterResource(id = R.drawable.gmail_logo), contentDescription = "Logo GMAIL")
            }
            Column (
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.6f),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Redactar Correo", color = Color.Black, fontSize = 25.sp, textAlign = TextAlign.Center)
            }
            Column (
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.2f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .weight(0.8f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black,
                    focusedContainerColor = FondoIndvCards,
                    unfocusedContainerColor = FondoIndvCards
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp))
                    .border(1.dp, ColorLogo, RoundedCornerShape(15.dp))
                    .weight(0.1f),
                label = { Text(text = "Correo", color = Color.Black) },
                value = correoContacto,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                onValueChange = { correoContacto = it }
            )
            TextField(
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black,
                    focusedContainerColor = FondoIndvCards,
                    unfocusedContainerColor = FondoIndvCards
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp))
                    .border(1.dp, ColorLogo, RoundedCornerShape(16.dp))
                    .weight(0.1f),
                label = { Text(text = "Asunto", color = Color.Black) },
                value = asuntoCorreo,
                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                onValueChange = { asuntoCorreo = it }
            )
            TextField(
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black,
                    focusedContainerColor = FondoIndvCards,
                    unfocusedContainerColor = FondoIndvCards
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp))
                    .border(1.dp, ColorLogo, RoundedCornerShape(15.dp))
                    .weight(0.7f),
                label = { Text(text = "Mensaje", color = Color.Black) },
                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                value = mensajeCorreo,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                onValueChange = { mensajeCorreo = it }
            )
        }
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier.wrapContentSize(),
                colors = ButtonDefaults.buttonColors(containerColor = FondoTarjetaInception),
                onClick = {
                correoContacto = ""
                asuntoCorreo = ""
                mensajeCorreo = ""
            }) {
                Text(text = "Limpiar", color = Color.Black, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                modifier = Modifier.wrapContentSize(),
                colors = ButtonDefaults.buttonColors(containerColor = FondoTarjetaInception),
                onClick = {
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
                Text(text = "Enviar", color = Color.Black, fontSize = 16.sp)
            }
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