package com.example.regalanavidad.modelos

data class Tarea(
    val id:String = generateRandomId(),
    var rol:String = "",
    var descripcion:String = "",
    var fechaLimite:String = "",
    var completada:Boolean = false
)
fun generateRandomId(): String {
    val charPool: List<Char> = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    val randomString = (1..11)
        .map { charPool.random() }
        .joinToString("")
    return randomString
}
