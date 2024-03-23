package com.example.regalanavidad.voluntarioScreens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun InformacionScreen(){
    LazyColumn {
        item { 
            Text(text = "¿Qué es Regala Navidad?")
        }
    }
}