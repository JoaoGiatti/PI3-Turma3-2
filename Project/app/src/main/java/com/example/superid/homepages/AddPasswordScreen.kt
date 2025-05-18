package com.example.superid.homepages

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.superid.model.PasswordItem
import kotlinx.coroutines.launch

@Composable
fun AddPasswordScreen(
    navController: NavController,
    onPasswordAdded: () -> Unit = {}
) {
    val viewModel: PasswordViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Sites Web") }

    // Categorias padrão (incluindo Sites Web como obrigatória)
    val defaultCategories = listOf("Sites Web", "Aplicativos", "Teclados de Acesso Físico", "Outros")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFF1E1E2C), shape = RoundedCornerShape(16.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Yellow), shape = RoundedCornerShape(50)) {
                Text("Nova Senha", color = Color.Black)
            }
            Button(onClick = {}, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent), shape = RoundedCornerShape(50)) {
                Text("Nova Categoria", color = Color.Yellow)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.White,
                focusedBorderColor = Color.Yellow,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Yellow,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.Yellow
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = { Text("Login") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.White,
                focusedBorderColor = Color.Yellow,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Yellow,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.Yellow
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.White,
                focusedBorderColor = Color.Yellow,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.Yellow,
                unfocusedLabelColor = Color.Gray,
                cursorColor = Color.Yellow
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        DropdownMenuBox(
            options = defaultCategories,
            selectedOption = selectedCategory,
            onOptionSelected = { selectedCategory = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val item = PasswordItem(
                    title = title,
                    login = login,
                    password = password,
                    category = selectedCategory
                )
                coroutineScope.launch {
                    try {
                        viewModel.addPassword(item)
                        onPasswordAdded()
                        title = ""
                        login = ""
                        password = ""
                        Toast.makeText(context, "Senha salva com sucesso!", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Erro ao salvar: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Yellow)
        ) {
            Text("Salvar", color = Color.Black)
        }
    }
}

@Composable
fun DropdownMenuBox(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.White
            )
        ) {
            Text(selectedOption)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.DarkGray)
        ) {
            options.forEach { label ->
                DropdownMenuItem(onClick = {
                    onOptionSelected(label)
                    expanded = false
                }) {
                    Text(label, color = Color.White)
                }
            }
        }
    }
}
