package com.example.regalanavidad.modelos

data class Producto(
    val nombre:String = "",
    val tipos:List<DetallesProducto> = emptyList(),
    val cantidadTotal:String = ""
)

data class DetallesProducto(
    val tipo:String = "",
    val cantidad:String = "",
)

data class ProductoResponse(
    val productos:List<Producto> = emptyList()
)
data class RequestPostRecaudacion(
    val spreadsheet_id: String,
    val sheet: String,
    val productos: List<Producto>
)