package com.example.superid.homepages

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superid.model.PasswordItem
import com.example.superid.repository.PasswordRepository
import kotlinx.coroutines.launch

class PasswordViewModel : ViewModel() {
    private val repository = PasswordRepository()
    private val _passwords = mutableStateListOf<PasswordItem>()
    val passwords: List<PasswordItem> = _passwords
    private val defaultCategories = listOf("Sites Web", "Aplicativos", "Teclados de Acesso Físico")

    private val _categories = mutableStateListOf<String>()
    val categories: List<String> = _categories

    init {
        loadPasswords()
        loadCategories()
    }

    private fun loadPasswords() {
        viewModelScope.launch {
            try {
                _passwords.clear()
                _passwords.addAll(repository.getPasswords())
            } catch (e: Exception) {}
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val userCategories = repository.getCategories()
                _categories.clear()
                _categories.addAll(defaultCategories)
                _categories.addAll(userCategories.filter {
                    !defaultCategories.contains(it)
                })
            } catch (e: Exception) {
                _categories.clear()
                _categories.addAll(defaultCategories)
            }
        }
    }

    suspend fun addPassword(item: PasswordItem) {
        try {
            val id = repository.addPassword(item)
            _passwords.add(item.copy(id = id))
        } catch (e: Exception) { throw e }
    }

    suspend fun updatePassword(item: PasswordItem) {
        try {
            repository.updatePassword(item)
            val index = _passwords.indexOfFirst { it.id == item.id }
            if (index != -1) _passwords[index] = item
        } catch (e: Exception) { throw e }
    }

    suspend fun deletePassword(item: PasswordItem) {
        try {
            repository.deletePassword(item.id)
            _passwords.removeIf { it.id == item.id }
        } catch (e: Exception) { throw e }
    }

    suspend fun addCategory(category: String) {
        try {
            repository.addCategory(category)
            if (!_categories.contains(category)) _categories.add(category)
        } catch (e: Exception) { throw e }
    }

    suspend fun deleteCategory(category: String) {
        try {
            repository.deleteCategory(category)
            _categories.remove(category)
        } catch (e: Exception) { throw e }
    }
}