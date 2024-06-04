package com.example.regalanavidad.loginSignUp

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.regalanavidad.R
import com.example.regalanavidad.sharedScreens.FirestoreManager
import com.example.regalanavidad.ui.theme.Purple40
import com.example.regalanavidad.modelos.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(navController: NavController, auth: FirebaseAuth){

    val context = LocalContext.current
    var usuario by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailExistente by remember { mutableStateOf(false) }
    val firestore = FirestoreManager()

    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(246, 246, 244))
            .padding(start = 25.dp, top = 25.dp, end = 25.dp),
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
                        color = Color(227, 162, 58),
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
                    focusedContainerColor = Color(247, 228, 198),
                    unfocusedContainerColor = Color(247, 228, 198),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.clip(RoundedCornerShape(50.dp))
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                label = {
                    Text(
                        text = "Correo",
                        color = Color(227, 162, 58),
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
                    focusedContainerColor = Color(247, 228, 198),
                    unfocusedContainerColor = Color(247, 228, 198),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.clip(RoundedCornerShape(50.dp))
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                label = {
                    Text(
                        text = "Contraseña",
                        color = Color(227, 162, 58),
                        fontSize = 14.sp
                    )
                },
                value = password,
                textStyle = TextStyle(color = Color.Black),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                onValueChange = { password = it },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(247, 228, 198),
                    unfocusedContainerColor = Color(247, 228, 198),
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
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
                                scope.launch {
                                    signUp(auth, usuario, email, password, context, navController)
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
                    containerColor = Color(227, 162, 58)
                )
            ) {
                Text(text = "Crear cuenta", fontSize = 16.sp)
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
                    color = Color(209, 154, 90)
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

private fun signUp(auth: FirebaseAuth, username: String, email:String, password:String, context: Context, navController: NavController){
    val firestore = FirestoreManager()
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

