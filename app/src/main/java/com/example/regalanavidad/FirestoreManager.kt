package com.example.regalanavidad

import android.util.Log
import com.example.regalanavidad.modelos.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreManager {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun insertaUsuario(usuario: Usuario){
        firestore.collection("usuarios").add(usuario).await()
    }

    suspend fun findUserByEmail(email:String): Usuario? {
        val listaUsuarios = firestore.collection("usuarios")
        val querySnapshot = listaUsuarios.whereEqualTo("correo", email).get().await()

        return if (!querySnapshot.isEmpty) {
            querySnapshot.documents[0].toObject(Usuario::class.java)
        } else {
            null
        }
    }

    suspend fun editaUsuario(usuario: Usuario){
        val refUsuario = usuario.uid.let { firestore.collection("usuarios").document(it) }
        refUsuario.update(usuario.toMap())
            .addOnSuccessListener { Log.i("ProfileScreen", "Usuario actualizado correctamente") }
            .addOnFailureListener{ Log.e("ProfileScreen", "Error actualizando al usuario") }
            .await()
    }

}