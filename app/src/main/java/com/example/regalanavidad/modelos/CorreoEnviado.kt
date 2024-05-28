package com.example.regalanavidad.modelos

data class CorreoEnviado(
    val enviadoPor: String,
    val correoContacto: String,
    val asuntoCorreo: String,
    val diaEnvio: String,
    val horaEnvio: String
)
