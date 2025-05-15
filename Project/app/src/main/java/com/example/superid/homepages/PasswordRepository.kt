package com.example.superid.repository

import com.example.superid.model.PasswordItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PasswordRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getPasswords(): List<PasswordItem> {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
        return db.collection("user_passwords")
            .document(userId)
            .collection("passwords")
            .get()
            .await()
            .documents
            .map { doc ->
                doc.toObject(PasswordItem::class.java)!!.copy(id = doc.id)
            }
    }

    suspend fun addPassword(item: PasswordItem): String {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
        val docRef = db.collection("user_passwords")
            .document(userId)
            .collection("passwords")
            .add(item)
            .await()
        return docRef.id
    }

    suspend fun updatePassword(item: PasswordItem) {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
        db.collection("user_passwords")
            .document(userId)
            .collection("passwords")
            .document(item.id)
            .set(item)
            .await()
    }

    suspend fun deletePassword(passwordId: String) {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
        db.collection("user_passwords")
            .document(userId)
            .collection("passwords")
            .document(passwordId)
            .delete()
            .await()
    }

    suspend fun getCategories(): List<String> {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
        return try {
            db.collection("user_categories")
                .document(userId)
                .get()
                .await()
                .get("categories") as? List<String> ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addCategory(category: String) {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
        val currentCategories = getCategories().toMutableList()
        if (!currentCategories.contains(category)) {
            currentCategories.add(category)
            db.collection("user_categories")
                .document(userId)
                .set(mapOf("categories" to currentCategories))
                .await()
        }
    }

    suspend fun deleteCategory(category: String) {
        val userId = auth.currentUser?.uid ?: throw Exception("Usuário não autenticado")
        val currentCategories = getCategories().toMutableList()
        currentCategories.remove(category)
        db.collection("user_categories")
            .document(userId)
            .set(mapOf("categories" to currentCategories))
            .await()
    }
}