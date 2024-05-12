package com.example.regalanavidad.modelos

data class Evento(
    var id: String = "",
    var titulo: String? = "",
    var descripcion: String = "",
    var startDate: String = "",
    var horaComienzo: String = "",
    var lugar: SitioRecogida = SitioRecogida(),
)