package com.example.regalanavidad.sharedScreens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun QueEs(){
    LazyColumn {
        item { 
            Text(text = "¿Qué es Regala Navidad?")
        }
    }
}

@Composable
fun ComoAyudar(){
    LazyColumn {
        item{
            Text(text = "¿Cómo puedo ayudar?")
        }
    }
}

@Composable
fun DatosYObjetivos(){
    LazyColumn {
        item{
            Text(text = "Datos y objetivos del proyecto:")
        }
    }
}