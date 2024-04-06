package com.example.regalanavidad.loginSignUp

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.example.regalanavidad.sharedScreens.Home
import com.example.regalanavidad.sharedScreens.MainActivity.Companion.email
import com.example.regalanavidad.R

@Composable
fun WaitForEmailVerificationScreen(auth: FirebaseAuth) {
    var user = auth.currentUser
    if (user != null) {
        email = user.email.toString()
    }
    val context = LocalContext.current

    Column (
    modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF3EBEB)),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
){
    Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
        GifImage()
    }
    Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = "Enlace de verificación enviado. Valida tu correo")
    }
    Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = {
                if (user != null) {
                    user?.sendEmailVerification()
                } else {
                    Toast.makeText(context, "No user is signed in", Toast.LENGTH_SHORT).show()
                }
            },
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Reenviar enlace")
        }
        //Poner botón de 'ya verificado'

}
    // Para usar corrutinas en un composable, usamos 'LaunchedEffect'
    LaunchedEffect(key1 = user?.isEmailVerified) { //El parámetro key1 asegura que el efecto se recompondrá cuando el valor de user?.isEmailVerified cambie.
        while (true) {
            user = auth.currentUser
            auth.currentUser?.reload() // Recargamos el usuario para comprobar cualquier actualización
            if (user?.isEmailVerified == true) {
                // Si el email se ha verificado, se navega a la home screen
                val intent = Intent(context, Home::class.java)
                intent.putExtra("correo", email)
                context.startActivity(intent)
                break // Sale del bucle while una vez se ha verificado
            } else {
                // Si el email no se ha verificado, lo comprobará de nuevo en 5 segundos
                delay(5000) // Wait for 5 seconds
            }
        }
    }
}
@Composable
fun GifImage(
    modifier: Modifier = Modifier,
) {
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current).data(data = R.drawable.astronauta_esperando).apply(block = {
                size(Size.ORIGINAL)
            }).build(), imageLoader = imageLoader
        ),
        contentDescription = null,
        modifier = modifier.fillMaxWidth(),
    )
}
