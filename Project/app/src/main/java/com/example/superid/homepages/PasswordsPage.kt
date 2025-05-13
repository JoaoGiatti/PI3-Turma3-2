package com.example.superid.homepages

import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.text.font.FontWeight
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.superid.R
import com.example.superid.model.PasswordItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun PasswordPage(navController: NavController, viewModel: PasswordViewModel = viewModel()) {
    val context = LocalContext.current
    val passwords by remember { derivedStateOf { viewModel.passwords } }

    var showAddPasswordDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    var newPasswordTitle by remember { mutableStateOf("") }
    var newPasswordValue by remember { mutableStateOf("") }
    var newPasswordLogin by remember { mutableStateOf("") }
    var newPasswordDescription by remember { mutableStateOf("") }
    var newPasswordCategory by remember { mutableStateOf("") }
    var newCategoryValue by remember { mutableStateOf("") }

    val colors = MaterialTheme.colors
    val typography = MaterialTheme.typography

    var isEmailVerified by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                isEmailVerified = user.isEmailVerified
            } else {
                Toast.makeText(context, "Erro ao verificar o e-mail.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(vertical = 24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        // ... (código anterior permanece igual até a parte dos diálogos)

        if (showAddPasswordDialog) {
            var expanded by remember { mutableStateOf(false) }
            val categories = listOf("Trabalho", "Pessoal", "Financeiro", "Outros")

            Dialog(
                onDismissRequest = { showAddPasswordDialog = false },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1C1C1E), shape = RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF1C1C1E), shape = RoundedCornerShape(50.dp)),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { },
                                shape = RoundedCornerShape(50),
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFFF00))
                            ) {
                                Text("Nova Senha", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                            TextButton(
                                onClick = { }
                            ) {
                                Text("Nova Categoria", color = Color(0xFFFFFF00), fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Text("Categoria", color = Color.Gray)
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = newPasswordCategory.ifEmpty { "Selecione uma categoria" },
                                color = if (newPasswordCategory.isEmpty()) Color.Gray else Color.White,
                                modifier = Modifier
                                    .clickable { expanded = true }
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp))
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown")
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                categories.forEach { category ->
                                    DropdownMenuItem(onClick = {
                                        newPasswordCategory = category
                                        expanded = false
                                    }) {
                                        Text(text = category, color = Color.White)
                                    }
                                }
                            }
                        }
                        Divider(color = Color(0xFFFFFF00), thickness = 1.dp)
                        Spacer(Modifier.height(8.dp))

                        TextField(
                            value = newPasswordTitle,
                            onValueChange = { newPasswordTitle = it },
                            placeholder = { Text("Nome da Senha", color = Color.Gray) },
                            singleLine = true,
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.White,
                                backgroundColor = Color.Transparent,
                                unfocusedIndicatorColor = Color(0xFFFFFF00),
                                focusedIndicatorColor = Color(0xFFFFFF00)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        TextField(
                            value = newPasswordLogin,
                            onValueChange = { newPasswordLogin = it },
                            placeholder = { Text("Login", color = Color.Gray) },
                            singleLine = true,
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.White,
                                backgroundColor = Color.Transparent,
                                unfocusedIndicatorColor = Color(0xFFFFFF00),
                                focusedIndicatorColor = Color(0xFFFFFF00)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        TextField(
                            value = newPasswordValue,
                            onValueChange = { newPasswordValue = it },
                            placeholder = { Text("Senha", color = Color.Gray) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.White,
                                backgroundColor = Color.Transparent,
                                unfocusedIndicatorColor = Color(0xFFFFFF00),
                                focusedIndicatorColor = Color(0xFFFFFF00)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        TextField(
                            value = newPasswordDescription,
                            onValueChange = { newPasswordDescription = it },
                            placeholder = { Text("Descrição", color = Color.Gray) },
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.White,
                                backgroundColor = Color.Transparent,
                                unfocusedIndicatorColor = Color(0xFFFFFF00),
                                focusedIndicatorColor = Color(0xFFFFFF00)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (newPasswordTitle.isBlank() || newPasswordValue.isBlank()) {
                                    Toast.makeText(context, "Preencha pelo menos título e senha", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val newItem = PasswordItem(
                                    title = newPasswordTitle,
                                    password = newPasswordValue,
                                    login = newPasswordLogin,
                                    description = newPasswordDescription,
                                    category = newPasswordCategory
                                )
                                viewModel.addPassword(newItem)
                                showAddPasswordDialog = false
                                newPasswordTitle = ""
                                newPasswordValue = ""
                                newPasswordLogin = ""
                                newPasswordDescription = ""
                                newPasswordCategory = ""
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFFF00)),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Criar", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (showAddCategoryDialog) {
            Dialog(
                onDismissRequest = { showAddCategoryDialog = false },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1C1C1E), shape = RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            "Nova Categoria",
                            color = Color(0xFFFFFF00),
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        OutlinedTextField(
                            value = newCategoryValue,
                            onValueChange = { newCategoryValue = it },
                            label = { Text("Nome da Categoria", color = Color.Gray) },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = Color.White,
                                backgroundColor = Color.Transparent,
                                unfocusedBorderColor = Color(0xFFFFFF00),
                                focusedBorderColor = Color(0xFFFFFF00)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { showAddCategoryDialog = false }
                            ) {
                                Text("Cancelar", color = Color(0xFFFFFF00))
                            }

                            Spacer(Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    Toast.makeText(context, "Categoria adicionada: $newCategoryValue", Toast.LENGTH_SHORT).show()
                                    newCategoryValue = ""
                                    showAddCategoryDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFFF00))
                            ) {
                                Text("Salvar", color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PasswordCard(
    item: PasswordItem,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 8.dp),
        backgroundColor = Color(0xFF252525),
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    color = Color.White,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .height(13.dp)
                        .background(Color.Black)
                )

                Text(
                    text = item.password,
                    color = Color.White,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                )
            }

            IconButton(onClick = onEdit) {
                Image(
                    painter = painterResource(id = R.drawable.btn_edit),
                    contentDescription = "Editar",
                    modifier = Modifier.size(34.dp)
                )
            }
            IconButton(onClick = onDelete) {
                Image(
                    painter = painterResource(id = R.drawable.btn_exclude),
                    contentDescription = "Deletar",
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}


