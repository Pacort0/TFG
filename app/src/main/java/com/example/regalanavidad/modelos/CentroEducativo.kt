package com.example.regalanavidad.modelos

data class CentroEducativo(
    var nombreCentro: String = "",
    var numeroCentro: String = "",
    var correoCentro: String = "",
    var tareaCentro: String = "",
    var estadoCentro: String = ""
) {
    fun toCentroEducativoRequest(): CentroEducativoRequest{
        return CentroEducativoRequest(
            nombreCentro = nombreCentro,
            tareaCentro = tareaCentro,
            estadoCentro = estadoCentro
        )
    }
}

data class CentroEducativoResponse(
    val centros: List<CentroEducativo>,
)

data class CentroEducativoRequest(
    val nombreCentro: String,
    var tareaCentro: String,
    val estadoCentro: String
)

data class RequestPostCentroEducativo(
    val spreadSheetId: String,
    val sheet: String,
    val centros: List<CentroEducativoRequest>
)