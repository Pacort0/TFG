package com.example.regalanavidad.loginSignUp

import android.content.Context
import android.net.ConnectivityManager
import android.util.Patterns
import android.widget.Toast
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.regalanavidad.R
import com.example.regalanavidad.modelos.Usuario
import com.example.regalanavidad.sharedScreens.auth
import com.example.regalanavidad.sharedScreens.firestore
import com.example.regalanavidad.sharedScreens.hayInternet
import com.example.regalanavidad.ui.theme.Blanco
import com.example.regalanavidad.ui.theme.ColorLogo
import com.example.regalanavidad.ui.theme.FondoApp
import com.example.regalanavidad.ui.theme.FondoIndvCards
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(navController: NavController){

    val context = LocalContext.current
    var usuario by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var hayInternet by remember { mutableStateOf(hayInternet(connectivityManager)) }
    var verPassword by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
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
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_regala_navidad_logo),
                contentDescription = "RegalaNavidadLogo",
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .size(270.dp),
            )
        }
        Column(
            modifier = Modifier.weight(0.6f),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                label = {
                    Text(
                        text = "Nombre de usuario",
                        color = ColorLogo,
                        fontSize = 14.sp
                    )
                },
                value = usuario,
                textStyle = TextStyle(color = Color.Black),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                onValueChange = { usuario = it },
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
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
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
                    Icon(
                        painter = painterResource(id = verPassword.let { if (it) R.drawable.ojo_ver else R.drawable.ojo_ocultar }),
                        contentDescription = "Ocultar contraseña",
                        tint = Color.Black,
                        modifier = Modifier.size(32.dp).clickable { verPassword = !verPassword }.padding(end = 10.dp))
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
                    imeAction = ImeAction.Done
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

            Spacer(modifier = Modifier.height(35.dp))
            Button(
                onClick = {
                    if(email.isNotEmpty() and password.isNotEmpty() and usuario.isNotEmpty()) {
                        if (isValidEmail(email)) {
                            if(password.length >= 6){
                                hayInternet = hayInternet(connectivityManager)
                                if (hayInternet){
                                    scope.launch {
                                        signUp(usuario, email, password, context, navController)
                                    }
                                } else {
                                    Toast.makeText(context, "No hay internet", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "La contraseña debe tener 6 caracteres o más", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Correo introducido incorrecto", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Existen campos vacíos", Toast.LENGTH_SHORT).show()
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
                Text(text = "Crear cuenta", fontSize = 16.sp, color = Blanco)
            }

            Spacer(modifier = Modifier.height(10.dp))
            ClickableText(
                text = AnnotatedString("¿Ya tienes cuenta? Inicia sesión"),
                onClick = {
                    navController.navigate("inicio")
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
fun isValidEmail(inputEmail: CharSequence?): Boolean {
    return if (inputEmail == null) {
        false
    } else {
        Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()
    }
}

private fun signUp(username: String, email:String, password:String, context: Context, navController: NavController){
    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {task ->
        if(task.isSuccessful){
            val user = auth.currentUser!!
            user.sendEmailVerification()
                .addOnCompleteListener { verificationTask ->
                    if(verificationTask.isComplete){
                        navController.navigate("waitingScreen")
                        val usuario = Usuario(username, email, password, user.uid)
                        CoroutineScope(Dispatchers.IO).launch {
                            firestore.insertaUsuario(usuario)
                        }
                    }
                }
        } else{ //si no se consigue iniciar sesión
            if (task.exception is FirebaseAuthUserCollisionException) {
                Toast.makeText(context, "Ese correo ya está en uso", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error creando la cuenta: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

