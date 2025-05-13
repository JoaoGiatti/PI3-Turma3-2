package com.example.superid.homepages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.superid.model.PasswordItem
import com.example.superid.homepages.PasswordViewModel

@Composable
fun AddPasswordScreen(navController: NavController, onPasswordAdded: () -> Unit = {}) {
    val viewModel: PasswordViewModel = viewModel()

    var title by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Apps") }

    val categories = listOf("Apps", "Sites", "Teclado Físico", "Outros")

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

        DropdownMenuBox(categories, selectedCategory, { selectedCategory = it })
        Spacer(modifier = Modifier.height(8.dp))

        CustomTextField("Nome da Senha", title, { title = it })
        CustomTextField("Login", login, { login = it })
        CustomTextField("Senha", password, { password = it }, PasswordVisualTransformation())
        CustomTextField("Descrição", description, { description = it })

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val item = PasswordItem(
                    title = title,
                    password = password,
                    category = selectedCategory
                )
                viewModel.addPassword(item)
                onPasswordAdded()
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Yellow),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50)
        ) {
            Text("Criar", color = Color.Black)
        }
    }
}

@Composable
fun CustomTextField(label: String, value: String, onValueChange: (String) -> Unit, visualTransformation: VisualTransformation = VisualTransformation.None) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = Color.White,
            focusedBorderColor = Color.Yellow,
            unfocusedBorderColor = Color.Gray
        ),
        visualTransformation = visualTransformation
    )
}

@Composable
fun DropdownMenuBox(options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selectedOption, color = Color.White)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF1E1E2C))
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
