package com.example.superid.homepages

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.superid.model.PasswordItem
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await
import android.util.Log
import androidx.compose.runtime.mutableStateOf

class PasswordViewModel : ViewModel() {
    // Listas observáveis
    private val _passwords = mutableStateListOf<PasswordItem>()
    val passwords: List<PasswordItem> = _passwords

    private val _categories = mutableStateListOf<String>()
    val categories: List<String> = _categories

    // Listeners do Firestore
    private var passwordsListener: ListenerRegistration? = null
    private var categoriesListener: ListenerRegistration? = null

    // Estados adicionais (opcional)
    private val _isLoading = mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading.value

    init {
        fetchPasswords()
        fetchCategories()
    }

    // ==================== FUNÇÕES DE SENHAS ==================== //
    private fun fetchPasswords() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        passwordsListener = FirebaseFirestore.getInstance()
            .collection("user_passwords")
            .document(userId)
            .collection("passwords")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("PasswordViewModel", "Erro ao buscar senhas: ${e.message}")
                    return@addSnapshotListener
                }

                snapshot?.documents?.forEach { doc ->
                    doc.toObject(PasswordItem::class.java)
                        ?.copy(id = doc.id)
                        ?.let { password ->
                            if (!_passwords.any { it.id == password.id }) {
                                _passwords.add(password)
                            }
                        }
                }
            }
    }

    fun addPassword(item: PasswordItem) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("user_passwords")
            .document(userId)
            .collection("passwords")
            .add(item)
            .addOnFailureListener { e ->
                Log.e("PasswordViewModel", "Erro ao adicionar senha: ${e.message}")
            }
    }

    fun deletePassword(passwordItem: PasswordItem) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        Firebase.firestore.collection("user_passwords")
            .document(userId)
            .collection("passwords")
            .document(passwordItem.id)
            .delete()
            .addOnFailureListener { e ->
                Log.e("PasswordViewModel", "Erro ao deletar senha: ${e.message}")
            }
    }

    // ==================== FUNÇÕES DE CATEGORIAS ==================== //
    private fun fetchCategories() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        categoriesListener = FirebaseFirestore.getInstance()
            .collection("user_passwords")
            .document(userId)
            .collection("categories")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("PasswordViewModel", "Erro ao buscar categorias: ${e.message}")
                    return@addSnapshotListener
                }

                _categories.clear()
                snapshot?.documents?.forEach { doc ->
                    doc.getString("name")?.let { name ->
                        if (!_categories.contains(name)) {
                            _categories.add(name)
                        }
                    }
                }

                // Adiciona categorias padrão se a lista estiver vazia
                if (_categories.isEmpty()) {
                    _categories.addAll(listOf("Trabalho", "Pessoal", "Financeiro", "Outros"))
                }
            }
    }

    suspend fun addCategory(categoryName: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Validação
        if (categoryName.isBlank()) {
            Log.w("PasswordViewModel", "Nome da categoria vazio!")
            return
        }

        if (_categories.contains(categoryName)) {
            Log.w("PasswordViewModel", "Categoria já existe!")
            return
        }

        try {
            _isLoading.value = true
            // Adiciona no Firestore
            FirebaseFirestore.getInstance()
                .collection("user_passwords")
                .document(userId)
                .collection("categories")
                .add(hashMapOf("name" to categoryName))
                .await()

            // Atualiza a lista local (já é feito pelo listener, mas garantimos aqui)
            if (!_categories.contains(categoryName)) {
                _categories.add(categoryName)
            }
        } catch (e: Exception) {
            Log.e("PasswordViewModel", "Erro ao adicionar categoria: ${e.message}")
            throw e
        } finally {
            _isLoading.value = false
        }
    }

    // ==================== LIMPEZA ==================== //
    override fun onCleared() {
        super.onCleared()
        passwordsListener?.remove()
        categoriesListener?.remove()
    }
}
