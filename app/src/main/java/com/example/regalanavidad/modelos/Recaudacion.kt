package com.example.regalanavidad.modelos

data class Producto(
    val nombre:String = "",
    var tipos:List<DetallesProducto> = emptyList(),
    var cantidadTotal:String = ""
)

data class DetallesProducto(
    val tipo:String = "",
    var cantidad:String = "",
)

data class ProductoResponse(
    val productos:List<Producto> = emptyList()
)
data class RequestPostRecaudacion(
    val spreadsheet_id: String,
    val sheet: String,
    val productos: List<Producto>
)