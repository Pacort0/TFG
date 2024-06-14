package com.example.regalanavidad.sharedScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.regalanavidad.ui.theme.FondoApp

@Composable
fun QueEs(){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(FondoApp)){
        LazyColumn {
            item {
                Text(text = "¿Qué es Regala Navidad?", color = Color.Black)
            }
        }
    }
}

@Composable
fun ComoAyudar(){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(FondoApp)) {
        LazyColumn {
            item {
                Text(text = "¿Cómo puedo ayudar?", color = Color.Black)
            }
        }
    }
}

@Composable
fun DatosYObjetivos(){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(FondoApp)) {
        LazyColumn {
            item {
                Text(text = "Datos y objetivos del proyecto", color = Color.Black)
            }
        }
    }
}