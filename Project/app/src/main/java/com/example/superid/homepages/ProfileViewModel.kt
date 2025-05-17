package com.example.superid.profile

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.superid.model.UserItem

class ProfileViewModel : ViewModel() {
    var userItem = mutableStateOf(UserItem())
        private set

    init {
        fetchUserInfo()
    }

    private fun fetchUserInfo() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users_data")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val item = document.toObject(UserItem::class.java)
                item?.let {
                    userItem.value = it
                }
            }
            .addOnFailureListener {
                // Log para depurar se algo falhar
                println("Erro ao buscar usuário: ${it.message}")
            }
    }
}

