package com.example.regalanavidad.modelos

data class DonacionItem(
    var tipo:String = "",
    var cantidad:String = ""
)
data class DonacionResponse(
    val donaciones: List<DonacionItem>,
)

