package com.example.regalanavidad.modelos

data class CentroEducativo(
    var nombreCentro: String = "",
    var numeroCentro: String = "",
    var correoCentro: String = "",
    var tareaCentro: String = "",
    var estadoCentro: String = ""
)

data class CentroEducativoResponse(
    val centros: List<CentroEducativo>,
)