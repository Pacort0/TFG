package com.example.regalanavidad.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.regalanavidad.modelos.Tarea
import com.example.regalanavidad.sharedScreens.firestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TareasViewModel : ViewModel() {
    private val _tareas = MutableStateFlow<List<Tarea>>(emptyList())
    val tareas: StateFlow<List<Tarea>> = _tareas.asStateFlow()
    init {
        viewModelScope.launch {
            while(true){
                _tareas.value = firestore.getTareas()
                Log.d("TareasVM", "Tareas actualizadas desde el bucle")
                delay(180000)
            }
        }
    }
    fun cargarTareas(){
        viewModelScope.launch {
            _tareas.value = firestore.getTareas()
            Log.d("TareasVM", "Tareas actualizadas desde la funcion")
        }
    }
}

