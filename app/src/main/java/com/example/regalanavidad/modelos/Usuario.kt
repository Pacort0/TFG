package com.example.regalanavidad.modelos

import android.media.Image
import com.example.regalanavidad.R

data class Usuario (
    var nombre: String = "",
    val correo: String = "",
    val password: String = "",
    val uid: String = "",
    val rango: Int = 0,
    val nombreRango:String = "Voluntario",
    val pfp:Int = R.drawable.scoutdefecto
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "nombre" to nombre,
            "correo" to correo,
            "password" to password,
            "uid" to uid,
            "rango" to rango,
            "nombreRango" to nombreRango,
            "pfp" to pfp
        )
    }
}
