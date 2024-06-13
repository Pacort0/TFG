package com.example.regalanavidad.modelos

data class Gasto(
    val motivoGasto: String = "",
    val fechaGasto: String = "",
    val cantidadGasto: String = "",
    val pagadoPor: String = ""
)

data class GastoResponse(
    val total: String = "",
    val gastos: List<Gasto> = emptyList()
)

data class RequestPostGasto(
    val spreadSheetId: String,
    val sheet: String,
    val gasto: Gasto
)
