package com.example.regalanavidad.sharedScreens

import android.util.Log
import com.example.regalanavidad.modelos.Evento
import com.example.regalanavidad.modelos.SitioRecogida
import com.example.regalanavidad.modelos.Tarea
import com.example.regalanavidad.modelos.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

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

    suspend fun getUsers(): MutableList<Usuario> {
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

    suspend fun getEventos(): List<Evento> {
        val querySnapshot = firestore.collection("eventos").get().await()
        return querySnapshot.toObjects(Evento::class.java)
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
}
