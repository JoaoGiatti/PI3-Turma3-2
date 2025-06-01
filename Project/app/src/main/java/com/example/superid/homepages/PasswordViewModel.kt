// Declaração do pacote
package com.example.superid.homepages

// Importações necessárias
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superid.model.PasswordItem
import com.example.superid.repository.PasswordRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciamento de senhas e categorias
 * Responsável por:
 * - Carregar e gerenciar a lista de senhas
 * - Gerenciar categorias de senhas
 * - Operações CRUD (Create, Read, Update, Delete) de senhas
 * - Operações de adição/remoção de categorias
 */
class PasswordViewModel : ViewModel() {
    // Repositório para operações com o banco de dados/fonte de dados
    private val repository = PasswordRepository()

    // Lista observável de senhas (estado interno)
    private val _passwords = mutableStateListOf<PasswordItem>()
    // Lista pública de senhas (somente leitura)
    val passwords: List<PasswordItem> = _passwords

    // Categorias padrão do sistema
    private val defaultCategories = listOf("Sites Web", "Aplicativos", "Teclados de Acesso Físico")

    // Lista observável de categorias (estado interno)
    private val _categories = mutableStateListOf<String>()
    // Lista pública de categorias (somente leitura)
    val categories: List<String> = _categories

    /**
     * Bloco de inicialização do ViewModel
     * Carrega as senhas e categorias quando o ViewModel é criado
     */
    init {
        loadPasswords()
        loadCategories()
    }

    /**
     * Carrega as senhas do repositório de forma assíncrona
     * Atualiza a lista interna (_passwords) com os dados obtidos
     */
    private fun loadPasswords() {
        viewModelScope.launch {
            try {
                _passwords.clear()
                _passwords.addAll(repository.getPasswords())
            } catch (_: Exception) {
                // Ignora erros silenciosamente (poderia ser melhorado com tratamento de erros)
            }
        }
    }

    /**
     * Carrega as categorias do repositório de forma assíncrona
     * Combina categorias padrão com categorias personalizadas do usuário
     */
    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val userCategories = repository.getCategories()
                _categories.clear()
                // Adiciona primeiro as categorias padrão
                _categories.addAll(defaultCategories)
                // Adiciona categorias do usuário que não são padrão
                _categories.addAll(userCategories.filter { !defaultCategories.contains(it) })
            } catch (_: Exception) {
                // Em caso de erro, carrega apenas as categorias padrão
                _categories.clear()
                _categories.addAll(defaultCategories)
            }
        }
    }

    /**
     * Adiciona uma nova senha
     * @param item Objeto PasswordItem contendo os dados da senha
     * @throws Exception Se ocorrer um erro durante a operação
     */
    suspend fun addPassword(item: PasswordItem) {
        try {
            // Adiciona no repositório e obtém o ID gerado
            val id = repository.addPassword(item)
            // Adiciona na lista interna com o ID atualizado
            _passwords.add(item.copy(id = id))
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Atualiza uma senha existente
     * @param item Objeto PasswordItem com os dados atualizados
     * @throws Exception Se ocorrer um erro durante a operação
     */
    suspend fun updatePassword(item: PasswordItem) {
        try {
            // Atualiza no repositório
            repository.updatePassword(item)
            // Encontra e atualiza na lista interna
            val index = _passwords.indexOfFirst { it.id == item.id }
            if (index != -1) _passwords[index] = item
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Remove uma senha
     * @param item Objeto PasswordItem a ser removido
     * @throws Exception Se ocorrer um erro durante a operação
     */
    suspend fun deletePassword(item: PasswordItem) {
        try {
            // Remove do repositório
            repository.deletePassword(item.id)
            // Remove da lista interna
            _passwords.removeIf { it.id == item.id }
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Adiciona uma nova categoria
     * @param category Nome da categoria a ser adicionada
     * @throws Exception Se ocorrer um erro durante a operação
     */
    suspend fun addCategory(category: String) {
        try {
            // Adiciona no repositório
            repository.addCategory(category)
            // Adiciona na lista interna se não existir
            if (!_categories.contains(category)) _categories.add(category)
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Remove uma categoria existente
     * @param category Nome da categoria a ser removida
     * @throws Exception Se ocorrer um erro durante a operação
     */
    suspend fun deleteCategory(category: String) {
        try {
            // Remove do repositório
            repository.deleteCategory(category)
            // Remove da lista interna
            _categories.remove(category)
        } catch (e: Exception) {
            throw e
        }
    }
}