package com.example.regalanavidad.modelos

data class Evento(
    var id: String = "",
    var titulo: String = "",
    var descripcion: String = "",
    var startDate: String = "01/01/2021",
    var horaComienzo: String = "",
    var lugar: SitioRecogida = SitioRecogida(),
)