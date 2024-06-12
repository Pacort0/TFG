package com.example.regalanavidad.loginSignUp

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.regalanavidad.R
import com.example.regalanavidad.sharedScreens.auth
import com.example.regalanavidad.sharedScreens.firestore
import com.example.regalanavidad.sharedScreens.hayInternet
import com.example.regalanavidad.ui.theme.Blanco
import com.example.regalanavidad.ui.theme.ColorLogo
import com.example.regalanavidad.ui.theme.FondoApp
import com.example.regalanavidad.ui.theme.FondoIndvCards
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PantallaOlvidoContrasena(navController: NavController){
    var email by remember{ mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var emailExistente by remember{ mutableStateOf(false) }
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var hayInternet by remember { mutableStateOf(hayInternet(connectivityManager)) }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .padding(start = 25.dp, top = 25.dp, end = 25.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Column(
            modifier = Modifier.weight(0.4f),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_regala_navidad_logo),
                contentDescription = "RegalaNavidadLogo",
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .size(270.dp),
            )
        }
        Column (
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(0.6f)
        ){
            Text(text = "Restablecer la contraseña",
                style = TextStyle(
                    fontSize = 25.sp,
                    color = ColorLogo
                )
            )
            Spacer(modifier = Modifier.height(50.dp))
            TextField(label = {
                Text(text = "Correo electrónico",
                    color = ColorLogo,
                    fontSize = 14.sp) },
                value = email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                onValueChange = {email = it},
                textStyle = TextStyle(color = Color.Black),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = FondoIndvCards,
                    unfocusedContainerColor = FondoIndvCards,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.clip(RoundedCornerShape(50.dp))
            )

            Spacer(modifier = Modifier.height(30.dp))
            Button(onClick = {
                if(email.isNotEmpty()){
                    if(isValidEmail(email)){
                        hayInternet = hayInternet(connectivityManager)
                        if (hayInternet){
                            scope.launch {
                                emailExistente = withContext(Dispatchers.IO) {
                                    firestore.comprobarCorreo(email)
                                }
                                if (emailExistente) {
                                    Toast.makeText(context, "Correo electrónico no registrado", Toast.LENGTH_SHORT).show()
                                } else {
                                    auth.sendPasswordResetEmail(email)
                                    Toast.makeText(context, "Correo electrónico enviado", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "No hay internet", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Introduzca un correo electrónico válido", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Introduzca un correo electrónico", Toast.LENGTH_SHORT).show()
                }
            },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 40.dp)
                    .clip(RoundedCornerShape(50.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorLogo
                )
            ) { Text(text = "Recuperar contraseña", fontSize = 16.sp, color = Blanco) }

            Spacer(modifier = Modifier.height(10.dp))

            ClickableText(
                text = AnnotatedString("Volver a la pantalla principal"),
                onClick = {
                    navController.navigate("inicio")
                },
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Default,
                    textDecoration = TextDecoration.Underline,
                    color = ColorLogo,
                    textAlign = TextAlign.End
                )
            )
        }
    }
}