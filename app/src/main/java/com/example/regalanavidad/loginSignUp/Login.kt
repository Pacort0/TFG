package com.example.regalanavidad.loginSignUp

import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.regalanavidad.Home
import com.example.regalanavidad.ui.theme.Purple40
import com.example.regalanavidad.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun Login(navController: NavController, auth: FirebaseAuth) {

    val currentUser = auth.currentUser
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    if (currentUser != null && currentUser.isEmailVerified) {
        email = currentUser.email.toString()
        val intent = Intent(context, Home::class.java)
        intent.putExtra("correo", email)
        context.startActivity(intent)
    } else {
        auth.signOut()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF3EBEB)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_regala_navidad_logo),
                contentDescription = "RegalaNavidad",
                modifier = Modifier.size(145.dp)
            )
            Text(
                text = "Regala Navidad",
                style = TextStyle(fontSize = 36.sp, color = Purple40)
            )

            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = { Text(text = "Correo") },
                value = email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = { email = it })

            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = { Text(text = "Contraseña") },
                value = password,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = { password = it })

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    if (email.isNotEmpty() and password.isNotEmpty()) {
                        if (isValidEmail(email)) {
                            if (password.length >= 6) {
                                scope.launch {
                                    iniciarSesion(navController, auth, email, password, context)
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
                        Toast.makeText(context, "Uno de los campos está vacío", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 40.dp) // Adjust padding as needed
            ) {
                Text(text = "Iniciar sesión")
            }

            Spacer(modifier = Modifier.height(20.dp))
            ClickableText(
                text = AnnotatedString("¿Olvidaste tu contraseña?"),
                onClick = {
                    navController.navigate("recuperaPassword")
                },
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Default,
                    textDecoration = TextDecoration.Underline,
                    color = Purple40
                )
            )

            Spacer(modifier = Modifier.height(20.dp))
            ClickableText(
                text = AnnotatedString("¿No tienes una cuenta? Regístrate"),
                onClick = {
                    navController.navigate("registroCuenta")
                },
                style = TextStyle(
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Default,
                    textDecoration = TextDecoration.Underline,
                    color = Purple40
                )
            )
        }
    }
}

private fun iniciarSesion(navController: NavController, auth: FirebaseAuth, email: String, password: String, context: Context) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                if (auth.currentUser?.isEmailVerified == true) {
                    Toast.makeText(context, "Iniciando sesión", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, Home::class.java)
                    intent.putExtra("correo", email)
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Valida tu correo electrónico", Toast.LENGTH_SHORT).show()
                    navController.navigate("waitingScreen")
                }
            } else {
                // If sign in fails, display a message to the user.
                Toast.makeText(
                    context,
                    "Error iniciando sesión",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
}