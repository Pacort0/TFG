package com.example.regalanavidad.modelos

data class Gasto(
    val motivoGasto: String = "",
    val fechaGasto: String = "",
    val cantidadGasto: String = "",
    val pagadoPor: String = ""
)

data class GastoResponse(
    val gastos: List<Gasto>,
)

data class RequestPostGasto(
    val spreadsheet_id: String,
    val sheet: String,
    val gasto: Gasto
)
