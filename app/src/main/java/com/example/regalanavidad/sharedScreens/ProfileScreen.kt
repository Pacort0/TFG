package com.example.regalanavidad.sharedScreens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.regalanavidad.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    var settingsForm by remember { mutableStateOf(TextFieldValue(usuario.nombre)) }
    var isNameChanged by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3EBEB))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Profile picture
        Image(
            painter = painterResource(id = R.drawable.scoutdefecto),
            contentDescription = "foto de perfil",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(15.dp))

        // Editable username
        OutlinedTextField(
            value = settingsForm,
            onValueChange = {
                settingsForm = it
                isNameChanged = it.text != usuario.nombre
            },
            label = { Text(text = "Nombre de usuario") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(text = usuario.correo)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = usuario.nombreRango)

        Spacer(modifier = Modifier.height(15.dp))

        // Clickable "Cerrar sesión" text
        ClickableText(
            text = AnnotatedString("Cerrar sesión"),
            onClick = {
                auth.signOut()
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }
        )

        // Show the Save button if name is changed and not empty
        if (isNameChanged && settingsForm.text.isNotBlank()) {
            Button(
                onClick = {
                    usuario.nombre = settingsForm.text
                    Toast.makeText(context, "Cambiando nombre", Toast.LENGTH_SHORT).show()
                    CoroutineScope(Dispatchers.IO).launch {
                        firestore.editaUsuario(usuario)
                        isNameChanged = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(text = "Guardar cambios")
            }
        }
    }
}