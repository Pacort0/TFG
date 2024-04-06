package com.example.regalanavidad.sharedScreens

import android.util.Log
import com.example.regalanavidad.modelos.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreManager {
    private val firestore = FirebaseFirestore.getInstance()
    private val listaUsuarios = firestore.collection("usuarios")

    suspend fun insertaUsuario(usuario: Usuario) {
        firestore.collection("usuarios").add(usuario).await()
    }

    suspend fun findUserByEmail(email: String): Usuario? {
        val querySnapshot = listaUsuarios.whereEqualTo("correo", email).get().await()

        return if (!querySnapshot.isEmpty) {
            querySnapshot.documents[0].toObject(Usuario::class.java)
        } else {
            null
        }
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
}
