package com.example.superid.repository

import com.example.superid.model.PasswordItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PasswordRepository {
    // Instância do Firestore
    private val db = FirebaseFirestore.getInstance()
    // Instância de autenticação Firebase
    private val auth = FirebaseAuth.getInstance()

    // Recupera todas as senhas do usuário autenticado
    suspend fun getPasswords(): List<PasswordItem> {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
        return db.collection("user_passwords")
            .document(userId)
            .collection("passwords")
            .get()
            .await()
            .documents
            .map { doc ->
                // Converte cada documento em PasswordItem e insere o ID do documento no objeto
                doc.toObject(PasswordItem::class.java)!!.copy(id = doc.id)
            }
    }

    // Adiciona uma nova senha ao Firestore e retorna o ID gerado
    suspend fun addPassword(item: PasswordItem): String {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
        val docRef = db.collection("user_passwords")
            .document(userId)
            .collection("passwords")
            .add(item)
            .await()
        return docRef.id
    }

    // Atualiza uma senha existente com base no seu ID
    suspend fun updatePassword(item: PasswordItem) {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
        db.collection("user_passwords")
            .document(userId)
            .collection("passwords")
            .document(item.id)
            .set(item) // Sobrescreve o documento com os novos dados
            .await()
    }

    // Remove uma senha da base de dados usando o ID
    suspend fun deletePassword(passwordId: String) {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
        db.collection("user_passwords")
            .document(userId)
            .collection("passwords")
            .document(passwordId)
            .delete()
            .await()
    }

    // Recupera a lista de categorias do usuário
    suspend fun getCategories(): List<String> {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
        return try {
            // Tenta obter o campo "categories" como lista de strings
            db.collection("user_categories")
                .document(userId)
                .get()
                .await()
                .get("categories") as? List<String> ?: emptyList()
        } catch (e: Exception) {
            // Retorna lista vazia em caso de erro
            emptyList()
        }
    }

    // Adiciona uma nova categoria à lista do usuário, se ainda não existir
    suspend fun addCategory(category: String) {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
        val currentCategories = getCategories().toMutableList()
        if (!currentCategories.contains(category)) {
            currentCategories.add(category)
            // Salva a lista atualizada no Firestore
            db.collection("user_categories")
                .document(userId)
                .set(mapOf("categories" to currentCategories))
                .await()
        }
    }

    // Remove uma categoria da lista do usuário
    suspend fun deleteCategory(category: String) {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
        val currentCategories = getCategories().toMutableList()
        currentCategories.remove(category)
        // Atualiza a lista de categorias no Firestore
        db.collection("user_categories")
            .document(userId)
            .set(mapOf("categories" to currentCategories))
            .await()
    }
}
