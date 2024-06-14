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
fun ContactanosScreen(){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(FondoApp)) {
        LazyColumn {
            item {
                Text(text = "Contáctanos", color = Color.Black)
            }
        }
    }
}