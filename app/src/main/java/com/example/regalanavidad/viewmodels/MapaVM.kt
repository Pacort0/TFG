package com.example.regalanavidad.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.regalanavidad.modelos.SitioRecogida

class MapaVM:ViewModel() {
    var sitioRecogida = MutableLiveData<SitioRecogida>()
    var searchSitioRecogida = MutableLiveData<Boolean>()
}