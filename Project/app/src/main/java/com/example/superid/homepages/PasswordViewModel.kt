package com.example.superid.homepages

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.superid.model.PasswordItem
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

class PasswordViewModel : ViewModel() {
    private val _passwords = mutableStateListOf<PasswordItem>()
    val passwords: List<PasswordItem> = _passwords

    private var snapshotListener: ListenerRegistration? = null

    init {
        fetchPasswords()
    }

    // Função para buscar as senhas no Firestore
    private fun fetchPasswords() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        snapshotListener = FirebaseFirestore.getInstance()
            .collection("user_passwords")
            .document(userId)
            .collection("passwords")
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) {
                    // Caso haja erro ou snapshot vazio, não faz nada
                    return@addSnapshotListener
                }

                // Limpa a lista de senhas antes de adicionar as novas
                _passwords.clear()

                // Itera sobre os documentos retornados e adiciona à lista
                for (doc in snapshot.documents) {
                    val item = doc.toObject(PasswordItem::class.java)?.copy(id = doc.id)
                    item?.let { _passwords.add(it) }
                }
            }
    }

    // Função para adicionar uma senha ao Firestore
    fun addPassword(item: PasswordItem) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("user_passwords")
            .document(userId)
            .collection("passwords")
            .add(item)
    }
    fun deletePassword(passwordItem: PasswordItem) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        Firebase.firestore.collection("user_passwords")
            .document(userId)
            .collection("passwords")
            .document(passwordItem.id)
            .delete()
    }


    // Função para remover o listener quando o ViewModel for destruído
    override fun onCleared() {
        super.onCleared()
        snapshotListener?.remove()
    }
}
