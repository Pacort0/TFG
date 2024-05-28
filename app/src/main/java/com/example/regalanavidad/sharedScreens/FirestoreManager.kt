package com.example.regalanavidad.sharedScreens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.regalanavidad.modelos.CorreoEnviado
import com.example.regalanavidad.modelos.Evento
import com.example.regalanavidad.modelos.SitioRecogida
import com.example.regalanavidad.modelos.Tarea
import com.example.regalanavidad.modelos.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class FirestoreManager {
    private val firestore = FirebaseFirestore.getInstance()
    private val listaUsuarios = firestore.collection("usuarios")

    suspend fun insertaUsuario(usuario: Usuario) {
        firestore.collection("usuarios").add(usuario).await()
    }

    suspend fun insertaSitioRecogida(sitioRecogida: SitioRecogida){
        firestore.collection("sitiosRecogida").add(sitioRecogida).await()
    }

    suspend fun getUserByEmail(email: String): Usuario? {
        val querySnapshot = listaUsuarios.whereEqualTo("correo", email).get().await()

        return if (!querySnapshot.isEmpty) {
            querySnapshot.documents[0].toObject(Usuario::class.java)
        } else {
            null
        }
    }

    suspend fun getUsuarios(): MutableList<Usuario> {
        val usuarios = mutableListOf<Usuario>()
        val querySnapshot = firestore.collection("usuarios").get().await()
        for (document in querySnapshot) {
            val usuario = document.toObject(Usuario::class.java)
            usuarios += usuario
        }
        return usuarios
    }

    suspend fun getSitiosRecogida(): List<SitioRecogida> {
        val querySnapshot = firestore.collection("sitiosRecogida").get().await()
        return querySnapshot.toObjects(SitioRecogida::class.java)
    }

    suspend fun editaUsuario(usuario: Usuario) {
        val uid = auth.uid
        if (uid != null) {
            val querySnapshot = listaUsuarios.whereEqualTo("uid", usuario.uid).get().await()
            val idDocumentoUsuario = querySnapshot.documents[0].id

            val refUsuario = firestore.collection("usuarios").document(idDocumentoUsuario)
            try {
                refUsuario.update(usuario.toMap()).await()
                Log.d("ProfileScreen", "Usuario actualizado con éxito")
            } catch (e: Exception) {
                Log.w("ProfileScreen", "Error updating document", e)
            }
        } else {
            Log.w("ProfileScreen", "User UID is null. Unable to update document.")
        }
    }

    suspend fun eliminaSitioRecogida(sitioRecogida: SitioRecogida){
        val querySnapshot = firestore.collection("sitiosRecogida").whereEqualTo("nombreSitio", sitioRecogida.nombreSitio).get().await()
        val idDocumentoSitioRecogida = querySnapshot.documents[0].id
        val refSitioRecogida = firestore.collection("sitiosRecogida").document(idDocumentoSitioRecogida)
        try {
            refSitioRecogida.delete().await()
            Log.d("SitioRecogida", "Sitio de recogida eliminado con éxito")
        } catch (e: Exception) {
            Log.w("SitioRecogida", "Error eliminando sitio de recogida", e)
        }
    }

    suspend fun insertaEvento(evento: Evento){
        firestore.collection("eventos").add(evento).await()
        Log.d("Evento", "Evento insertado con éxito")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getEventos(): List<Evento> {
        val querySnapshot = firestore.collection("eventos").get().await()
        val eventos = querySnapshot.toObjects(Evento::class.java)

        val today = LocalDate.now()

        return eventos.mapNotNull { evento ->
            val fechaLocal = evento.startDate.toLocalDate()
            if (fechaLocal != null) {
                evento to fechaLocal
            } else {
                null
            }
        }.filter { it.second.isAfter(today) || it.second.isEqual(today) } // Filtra eventos futuros y el día actual
            .sortedBy { it.second } // Ordena por la fecha
            .map { it.first } // Devuelve solo los eventos
    }

    suspend fun eliminaEvento(evento: Evento){
        val querySnapshot = firestore.collection("eventos").whereEqualTo("id", evento.id).get().await()
        val idDocumentoEvento = querySnapshot.documents[0].id
        val refEvento = firestore.collection("eventos").document(idDocumentoEvento)
        try {
            refEvento.delete().await()
            Log.d("Evento", "Evento eliminado con éxito")
        } catch (e: Exception) {
            Log.w("Evento", "Error eliminando evento", e)
        }
    }
    suspend fun insertaTarea(tarea: Tarea){
        firestore.collection("tareas").add(tarea).await()
        Log.d("Tarea", "Tarea insertada con éxito")
    }
    suspend fun getTareas(): List<Tarea> {
        val querySnapshot = firestore.collection("tareas").get().await()
        return querySnapshot.toObjects(Tarea::class.java)
    }
    suspend fun editaTarea(tarea: Tarea) {
        val querySnapshot = firestore.collection("tareas").whereEqualTo("id", tarea.id).get().await()
        val idDocumentoTarea = querySnapshot.documents[0].id
        val refTarea = firestore.collection("tareas").document(idDocumentoTarea)
        try {
            refTarea.update(tarea.toMap()).await()
            Log.d("Tarea", "Tarea actualizada con éxito")
        } catch (e: Exception) {
            Log.w("Tarea", "Error actualizando tarea", e)
        }
    }
    suspend fun eliminaTarea(tarea: Tarea){
        val querySnapshot = firestore.collection("tareas").whereEqualTo("id", tarea.id).get().await()
        val idDocumentoTarea = querySnapshot.documents[0].id
        val refTarea = firestore.collection("tareas").document(idDocumentoTarea)
        try {
            refTarea.delete().await()
            Log.d("Tarea", "Tarea eliminada con éxito")
        } catch (e: Exception) {
            Log.w("Tarea", "Error eliminando tarea", e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getProximoEvento(): Evento? {
        val firestore = FirebaseFirestore.getInstance()
        val querySnapshot = firestore.collection("eventos").get().await()
        val eventos = querySnapshot.toObjects(Evento::class.java)

        val today = LocalDate.now()

        return eventos.mapNotNull { evento ->
            val fechaLocal = evento.startDate.toLocalDate()
            if (fechaLocal != null && (fechaLocal.isAfter(today) || fechaLocal.isEqual(today))) {
                evento to fechaLocal
            } else {
                null
            }
        }.minByOrNull { it.second }?.first
    }

    suspend fun getCorreosEnviados(): List<CorreoEnviado> {
        val querySnapshot = firestore.collection("correosEnviados").get().await()
        return querySnapshot.toObjects(CorreoEnviado::class.java)
    }

    suspend fun insertaCorreoEnviado(correoEnviado: CorreoEnviado){
        firestore.collection("correosEnviados").add(correoEnviado).await()
        Log.d("CorreoEnviado", "Correo enviado insertado con éxito")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun String.toLocalDate(): LocalDate? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            LocalDate.parse(this, formatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }
}
