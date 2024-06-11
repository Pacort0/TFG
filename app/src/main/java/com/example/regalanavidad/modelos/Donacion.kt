package com.example.regalanavidad.modelos

data class Donacion(
    var tipo:String = "",
    var cantidad:String = ""
)
data class DonacionResponse(
    val donaciones: List<Donacion> = emptyList()
)

