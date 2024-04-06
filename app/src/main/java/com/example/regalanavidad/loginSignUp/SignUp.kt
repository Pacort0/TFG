package com.example.regalanavidad.loginSignUp

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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

    val scope = rememberCoroutineScope() //Se define un 'alcance de corrutina', que estará asociada al ciclo de vida del componente de Compose en el que se encuentra,
                                         //siendo cancelada automáticamente cuando el componente asociado es eliminado o recompuesto
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3EBEB)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear cuenta",
            style = TextStyle(fontSize = 40.sp, color = Purple40)
            )

        Spacer(modifier = Modifier.height(50.dp))
        TextField(
            label = { Text(text = "Usuario")},
            value = usuario,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),            onValueChange = {usuario = it})

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = "Correo electrónico")},
            value = email,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),            onValueChange = {email = it})

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = "Contraseña")},
            value = password,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),            onValueChange = {password = it})
        
        Spacer(modifier = Modifier.height(30.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)){
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
                ) {
                Text(text = "Registrarse")
            }
        }
        Spacer(modifier = Modifier.height(40.dp))
        ClickableText(
            text = AnnotatedString("¿Ya tienes una cuenta? Iniciar sesión"),
            onClick = {
                navController.navigate("inicio")
            },
            style = TextStyle(
                fontSize = 16.sp,
                fontFamily = FontFamily.Default,
                textDecoration = TextDecoration.Underline,
                color = Purple40
            )
        )
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

