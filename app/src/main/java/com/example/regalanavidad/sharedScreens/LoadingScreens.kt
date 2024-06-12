package com.example.regalanavidad.sharedScreens

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.regalanavidad.R
import com.example.regalanavidad.ui.theme.FondoApp
import com.example.regalanavidad.ui.theme.FondoIndvCards
import com.example.regalanavidad.ui.theme.FondoTarjetaInception

@Composable
fun PantallaCarga(textoCargando:String){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = FondoTarjetaInception
        )
        Text(
            text = textoCargando,
            color = Color.Black,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun NoUbicacionScreen(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ubicacion_desactivada),
            contentDescription = "No Ubicación",
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "¡Vaya!", fontSize = 24.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Tienes la ubicación desactivda.\nActívala para poder acceder al mapa.",
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = FondoIndvCards
            ),
            onClick = onRetry
        ) {
            Text(text = "Reintentar", color = Color.Black, fontSize = 18.sp)
        }
    }
}

@Composable
fun NoInternetScreen(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.no_internet_icon),
            contentDescription = "No Internet",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "¡Vaya!", fontSize = 24.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "No hay conexión a Internet.\nConéctate para acceder a toda la información del proyecto.",
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = FondoIndvCards
            ),
            onClick = onRetry
        ) {
            Text(text = "Reintentar", color = Color.Black, fontSize = 18.sp)
        }
    }
}

@Composable
fun DeniedLocationScreen(onRequestPermissionAgain: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoApp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.no_location_icon),
            contentDescription = "No Ubicación",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(text = "¡Vaya!", fontSize = 24.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "No se ha encontrado ubicación.\nActívala para acceder a las ubicaciones del proyecto.",
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = FondoIndvCards
            ),
            onClick = onRequestPermissionAgain
        ) {
            Text(text = "Reintentar", color = Color.Black, fontSize = 18.sp)
        }
    }
}

fun hayInternet(connectivityManager: ConnectivityManager): Boolean {
    val activeNetwork = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}