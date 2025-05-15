// Arquivo: PasswordItem.kt
package com.example.superid.model

data class PasswordItem(
    var id: String = "",
    val title: String = "",
    val password: String = "",
    val category: String = "",
    val login: String = ""  // Novo campo
) {
    constructor() : this("", "", "", "", "")
}