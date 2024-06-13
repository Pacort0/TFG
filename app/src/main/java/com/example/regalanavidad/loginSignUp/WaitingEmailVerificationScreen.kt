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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.example.regalanavidad.R
import com.example.regalanavidad.sharedScreens.Home
import com.example.regalanavidad.sharedScreens.MainActivity.Companion.email
import com.example.regalanavidad.sharedScreens.auth
import com.example.regalanavidad.ui.theme.Blanco
import com.example.regalanavidad.ui.theme.ColorLogo
import com.example.regalanavidad.ui.theme.FondoApp
import kotlinx.coroutines.delay

@Composable
fun WaitForEmailVerificationScreen(navController: NavController) {
    var user = auth.currentUser
    if (user != null) {
        email = user.email.toString()
    }
    val context = LocalContext.current

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp)
            .padding(start = 25.dp, top = 25.dp, end = 25.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            GifImage()
        }

        Spacer(modifier = Modifier.height(20.dp))

        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Enlace de verificación enviado. Valida tu correo", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                if (user != null) {
                    user?.sendEmailVerification()
                    Toast.makeText(context, "Enlace reenviado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Usuario no registrado", Toast.LENGTH_SHORT).show()
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
            Text(text = "Reenviar enlace", color = Blanco)
        }
        Spacer(modifier = Modifier.height(15.dp))
        if (user == null) {
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
                delay(2500) // Espera 2.5 segundos
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
        contentScale = ContentScale.Fit
    )
}
