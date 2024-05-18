package com.example.regalanavidad.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.regalanavidad.modelos.Tarea
import kotlinx.coroutines.launch

class TareasViewModel : ViewModel() {
    val tareas = mutableStateOf(listOf<Tarea>())

    init {
        viewModelScope.launch {
            tareas.value = cargarTareasPendientes()
        }
    }

    private suspend fun cargarTareasPendientes(): List<Tarea> {
        /*TODO*/
    }
}
