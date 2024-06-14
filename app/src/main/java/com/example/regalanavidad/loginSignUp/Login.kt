package com.example.regalanavidad.loginSignUp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.regalanavidad.R
import com.example.regalanavidad.sharedScreens.Home
import com.example.regalanavidad.sharedScreens.auth
import com.example.regalanavidad.sharedScreens.firestore
import com.example.regalanavidad.sharedScreens.hayInternet
import com.example.regalanavidad.ui.theme.Blanco
import com.example.regalanavidad.ui.theme.ColorLogo
import com.example.regalanavidad.ui.theme.FondoApp
import com.example.regalanavidad.ui.theme.FondoIndvCards
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@Composable
fun Login(navController: NavController) {

    val currentUser = auth.currentUser
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var hayInternet by remember { mutableStateOf(hayInternet(connectivityManager)) }
    var verPassword by remember { mutableStateOf(false) }

    //Si el usuario ya inició sesión y verificó su correo, se le redirige a la pantalla principal
    if (currentUser != null && currentUser.isEmailVerified) {
        email = currentUser.email.toString()
        val intent = Intent(context, Home::class.java)
        intent.putExtra("correo", email)
        context.startActivity(intent)
    } else { //Si no, se cierra la sesión
        auth.signOut()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoApp)
                .padding(start = 25.dp, top = 50.dp, end = 25.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier.weight(0.4f),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {//Logo
                Image(
                    painter = painterResource(id = R.drawable.ic_regala_navidad_logo),
                    contentDescription = "RegalaNavidadLogo",
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .size(270.dp),
                )
            }
            //Formulario
            Column(
                modifier = Modifier.weight(0.6f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    label = {
                        Text(
                            text = "Correo",
                            color = ColorLogo,
                            fontSize = 14.sp
                        )
                    },
                    value = email,
                    textStyle = TextStyle(color = Color.Black),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next //Pasa al siguiente textfield
                    ),
                    onValueChange = { email = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = FondoIndvCards,
                        unfocusedContainerColor = FondoIndvCards,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.Black
                    ),
                    modifier = Modifier.clip(RoundedCornerShape(50.dp))
                )

                Spacer(modifier = Modifier.height(20.dp))

                TextField(
                    trailingIcon = { if (password.isNotEmpty() && password.isNotBlank()) {
                        //Icono para mostrar u ocultar la contraseña
                        Icon(
                            painter = painterResource(id = verPassword.let { if (it) R.drawable.ojo_ver else R.drawable.ojo_ocultar }),
                            contentDescription = "Ocultar contraseña",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { verPassword = !verPassword }
                                .padding(end = 10.dp)
                        )
                    }},
                    label = {
                        Text(
                            text = "Contraseña",
                            color = ColorLogo,
                            fontSize = 14.sp
                        )
                    },
                    value = password,
                    textStyle = TextStyle(color = Color.Black),
                    visualTransformation = verPassword.let { if (it) VisualTransformation.None else PasswordVisualTransformation() },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done //Cierra el teclado
                    ),
                    onValueChange = { password = it },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = FondoIndvCards,
                        unfocusedContainerColor = FondoIndvCards,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.Black
                        ),
                    modifier = Modifier.clip(RoundedCornerShape(50.dp))
                )

                Spacer(modifier = Modifier.height(10.dp))

                //Redirige a la pantalla de recuperar contraseña
                ClickableText(
                    text = AnnotatedString("¿Olvidaste tu contraseña?"),
                    onClick = {
                        navController.navigate("recuperaPassword")
                    },
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Default,
                        textDecoration = TextDecoration.Underline,
                        color = ColorLogo,
                        textAlign = TextAlign.End
                    )
                )

                Spacer(modifier = Modifier.height(35.dp))

                //Botón para iniciar sesión con todas las comprobaciones necesarias
                Button(
                    onClick = {
                        if (email.isNotEmpty() and password.isNotEmpty()) {
                            if (isValidEmail(email)) {
                                if (password.length >= 6) {
                                    hayInternet = hayInternet(connectivityManager)
                                    if (hayInternet){
                                        scope.launch {
                                            iniciarSesion(navController, email, password, context)
                                        }
                                    } else {
                                        Toast.makeText(context, "No hay Internet", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "La contraseña debe tener 6 caracteres o más",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "El correo introducido no es válido",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Uno de los campos está vacío",
                                Toast.LENGTH_SHORT
                            )
                                .show()
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
                ) {
                    Text(text = "Iniciar sesión", fontSize = 16.sp, color = Blanco)
                }

                Spacer(modifier = Modifier.height(10.dp))

                //Redirige a la pantalla de registro
                ClickableText(
                    text = AnnotatedString("¿No tienes una cuenta? Regístrate"),
                    onClick = {
                        navController.navigate("registroCuenta")
                    },
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Default,
                        textDecoration = TextDecoration.Underline,
                        color = ColorLogo
                    )
                )
            }
        }
    }

    //Si se ha cerrado sesión y se quiere ir para atrás, controlamos el comportamiento
    BackHandler {
        val paginaPreviaPila = navController.previousBackStackEntry
        if(paginaPreviaPila != null){
            if(paginaPreviaPila.destination.route != "Home" || paginaPreviaPila.destination.route != "profileScreen"){
                navController.popBackStack()
            } else {
                ActivityCompat.finishAffinity(context as Activity)
                exitProcess(0)
            }
        } else {
            ActivityCompat.finishAffinity(context as Activity)
            exitProcess(0)
        }
    }
}

//Función para iniciar sesión
private fun iniciarSesion(navController: NavController, email: String, password: String, context: Context) {
    val scope = CoroutineScope(Job() + Dispatchers.IO)
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //Si el correo está verificado, se inicia sesión
                if (auth.currentUser?.isEmailVerified == true) {
                    val intent = Intent(context, Home::class.java)
                    intent.putExtra("correo", email)
                    context.startActivity(intent)
                    Toast.makeText(context, "Iniciando sesión", Toast.LENGTH_SHORT).show()
                    scope.launch {
                        val usuario = firestore.getUserByEmail(auth.currentUser?.email.toString())
                        val usuarioNewPassword = usuario?.copy()
                        if (usuario != null) {
                            //Si se inicia sesión con una contraseña distinta a la que se tiene en la base de datos (se ha cambiado),
                            // se actualiza
                            if (usuario.password != password) {
                                if (usuarioNewPassword != null) {
                                    usuarioNewPassword.password = password
                                    firestore.editaUsuario(usuarioNewPassword)
                                }
                            }
                        }
                    }
                } else { //Si no, se le pide que verifique su correo
                    Toast.makeText(context, "Valida tu correo electrónico", Toast.LENGTH_SHORT).show()
                    navController.navigate("waitingScreen")
                }
            } else {
                Toast.makeText(
                    context,
                    "Error iniciando sesión",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
}