package com.example.superid.model

data class PasswordItem(
    val id: String = "",
    val title: String = "",
    val login: String = "",
    val password: String = "",
    val description: String = "",
    val category: String = "",
    val url: String = "",
    val accessToken: String = ""
) {
    // Construtor vazio para compatibilidade com Firebase
    constructor() : this("", "", "", "", "", "")
}