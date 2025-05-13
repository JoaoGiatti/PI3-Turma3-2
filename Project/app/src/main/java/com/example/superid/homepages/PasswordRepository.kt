package com.example.superid.repository

import com.example.superid.model.PasswordItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class   PasswordRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Função para adicionar a senha no banco dentro do usuário correto
    fun addPassword(passwordItem: PasswordItem) {
        val userId = auth.currentUser?.uid ?: return // Verifica se o usuário está autenticado

        db.collection("user_passwords")
            .document(userId)  // Usando o UID do usuário autenticado
            .collection("passwords")  // Subcoleção de senhas do usuário
            .add(passwordItem)
            .addOnSuccessListener {
                // Sucesso ao salvar a senha no Firestore
            }
            .addOnFailureListener {
                // Tratamento de erro
            }
    }

    // Função para obter as senhas do usuário
    fun getPasswords(onSuccess: (List<PasswordItem>) -> Unit, onFailure: (Exception) -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        db.collection("user_passwords")
            .document(userId)
            .collection("passwords")
            .get()
            .addOnSuccessListener { result ->
                val passwords = result.map { document ->
                    document.toObject(PasswordItem::class.java)
                }
                onSuccess(passwords)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
