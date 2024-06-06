package com.example.regalanavidad.viewmodels

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.regalanavidad.modelos.Evento
import com.example.regalanavidad.sharedScreens.FirestoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class EventosVM: ViewModel() {
    private val firestore = FirestoreManager()

    private val _proximoEvento = MutableStateFlow(Evento())
    val proximoEvento: StateFlow<Evento> = _proximoEvento.asStateFlow()
    init {
        viewModelScope.launch {
            _proximoEvento.value = firestore.getProximoEvento()!!
            Log.d("ProxEvento", "Proximo evento: ${_proximoEvento.value.titulo}")
        }
    }
}