package com.example.regalanavidad.modelos

data class Evento(
    var id: Int = 0,
    var titulo: String? = "",
    var startDate: String = "",
    var horaComienzo: String = "",
    var lugar: SitioRecogida = SitioRecogida(),
)