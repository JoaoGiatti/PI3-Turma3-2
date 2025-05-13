package com.example.superid.model

data class PasswordItem(
    val id: String = "",
    val title: String = "",
    val password: String = "",
    val login: String = "",       // Novo campo adicionado
    val description: String = "", // Novo campo adicionado
    val category: String = ""
) {
    // Construtor vazio para compatibilidade com Firebase
    constructor() : this("", "", "", "", "", "")
}