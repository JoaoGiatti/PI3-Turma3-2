package com.example.superid.homepages

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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val categories by remember { derivedStateOf { viewModel.categories } }

    var showAddPasswordDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showEditPasswordDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    var newPasswordTitle by remember { mutableStateOf("") }
    var newLoginValue by remember { mutableStateOf("") }
    var newPasswordValue by remember { mutableStateOf("") }
    var newPasswordCategory by remember { mutableStateOf(categories.firstOrNull() ?: "") }
    var newCategoryValue by remember { mutableStateOf("") }
    var categoryToDelete by remember { mutableStateOf("") }
    var itemToDelete by remember { mutableStateOf<PasswordItem?>(null) }

    var editingPassword by remember { mutableStateOf<PasswordItem?>(null) }
    var categoryExpanded by remember { mutableStateOf(false) }

    var isEmailVerified by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.reload()?.addOnCompleteListener { task ->
            isEmailVerified = task.isSuccessful && user.isEmailVerified
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Confirmar Exclusão", color = Color.White) },
            text = {
                val message = if (categoryToDelete.isNotEmpty()) {
                    "Tem certeza que deseja excluir a categoria '$categoryToDelete'?"
                } else {
                    "Tem certeza que deseja excluir '${itemToDelete?.title}'?"
                }
                Text(message, color = Color.White)
            },
            backgroundColor = Color(0xFF424242),
            shape = RoundedCornerShape(12.dp),
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        coroutineScope.launch {
                            try {
                                if (categoryToDelete.isNotEmpty()) {
                                    viewModel.deleteCategory(categoryToDelete)
                                    categoryToDelete = ""
                                }
                                itemToDelete?.let {
                                    viewModel.deletePassword(it)
                                    itemToDelete = null
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Erro ao excluir: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                ) {
                    Text("Confirmar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        categoryToDelete = ""
                        itemToDelete = null
                    }
                ) {
                    Text("Cancelar", color = Color.White)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .padding(vertical = 24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 32.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.menu),
                contentDescription = "Voltar",
                modifier = Modifier
                    .size(30.dp)
                    .clickable { (context as? ComponentActivity)?.finish() }
            )

            Spacer(modifier = Modifier.width(84.dp))

            Image(
                painter = painterResource(id = R.drawable.superid),
                contentDescription = "Logo SuperID",
                modifier = Modifier.height(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        Divider(color = Color.Gray, thickness = 1.dp)


            if (!isEmailVerified) {
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(
                            color = MaterialTheme.colors.error,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colors.error,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .height(40.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.alert),
                            contentDescription = "Alerta",
                            tint = Color.White,
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = "Valide seu email para usar todas as funções",
                            color = Color.White,
                            style = MaterialTheme.typography.subtitle2
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { showAddPasswordDialog = true },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFE2DA06),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text("Nova Senha", color = Color.Black)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = { showAddCategoryDialog = true },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFE2DA06),
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text("Nova Categoria", color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        Divider(color = Color.Gray, thickness = 0.3.dp, modifier = Modifier.padding(horizontal = 16.dp))

        val grouped = passwords.groupBy { it.category.ifEmpty { "Sem Categoria" } }

        LazyColumn {
            grouped.forEach { (category, items) ->
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = category,
                            color = Color(0xFF555555),
                            style = MaterialTheme.typography.subtitle1,
                            modifier = Modifier.weight(1f)
                        )

                        // Only show delete button for non-default categories
                        if (category != "Sem Categoria" &&
                            category != "Sites Web" &&
                            category != "Aplicativos" &&
                            category != "Teclados de Acesso Físico") {
                            IconButton(
                                onClick = {
                                    categoryToDelete = category
                                    showDeleteConfirmation = true
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.x),
                                    contentDescription = "Excluir",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                items(items) { item ->
                    PasswordCard(
                        item = item,
                        onDelete = {
                            itemToDelete = item
                            showDeleteConfirmation = true
                        },
                        onEdit = {
                            editingPassword = item
                            newPasswordTitle = item.title
                            newLoginValue = item.login
                            newPasswordValue = item.password
                            newPasswordCategory = item.category
                            showEditPasswordDialog = true
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(
                        color = Color.Gray,
                        thickness = 0.3.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }

    if (showAddPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showAddPasswordDialog = false },
            title = { Text("Adicionar Nova Senha", color = Color.White) },
            backgroundColor = Color(0xFF424242),
            text = {
                Column {
                    OutlinedTextField(
                        value = newPasswordTitle,
                        onValueChange = { newPasswordTitle = it },
                        label = { Text("Título", color = Color.White) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.White,
                            focusedBorderColor = Color.Yellow,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Yellow,
                            unfocusedLabelColor = Color.Gray,
                            cursorColor = Color.Yellow
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newLoginValue,
                        onValueChange = { newLoginValue = it },
                        label = { Text("Login", color = Color.White) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.White,
                            focusedBorderColor = Color.Yellow,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Yellow,
                            unfocusedLabelColor = Color.Gray,
                            cursorColor = Color.Yellow
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newPasswordValue,
                        onValueChange = { newPasswordValue = it },
                        label = { Text("Senha", color = Color.White) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.White,
                            focusedBorderColor = Color.Yellow,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Yellow,
                            unfocusedLabelColor = Color.Gray,
                            cursorColor = Color.Yellow
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = newPasswordCategory,
                            onValueChange = {},
                            label = { Text("Categoria", color = Color.White) },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Abrir menu",
                                    tint = Color.White
                                )
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = Color.White,
                                focusedBorderColor = Color.Yellow,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = Color.Yellow,
                                unfocusedLabelColor = Color.Gray
                            )
                        )

                        DropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF424242)) // Fundo escuro
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    onClick = {
                                        newPasswordCategory = category
                                        categoryExpanded = false
                                    },
                                    modifier = Modifier.background(Color(0xFF424242)) // Fundo escuro
                                ) {
                                    Text(
                                        text = category,
                                        color = Color.White, // Texto branco
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .alpha(0f)
                                .clickable { categoryExpanded = true }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPasswordTitle.isBlank() || newPasswordValue.isBlank() || newLoginValue.isBlank()) {
                            Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val newItem = PasswordItem(
                            title = newPasswordTitle,
                            login = newLoginValue,
                            password = newPasswordValue,
                            category = newPasswordCategory
                        )

                        coroutineScope.launch {
                            try {
                                viewModel.addPassword(newItem)
                                Toast.makeText(context, "Senha adicionada", Toast.LENGTH_SHORT).show()
                                newPasswordTitle = ""
                                newLoginValue = ""
                                newPasswordValue = ""
                                newPasswordCategory = categories.firstOrNull() ?: ""
                                showAddPasswordDialog = false
                            } catch (e: Exception) {
                                Toast.makeText(context, "Erro ao adicionar senha", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE2DA06))
                ) {
                    Text("Salvar", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddPasswordDialog = false
                        newPasswordTitle = ""
                        newLoginValue = ""
                        newPasswordValue = ""
                        newPasswordCategory = categories.firstOrNull() ?: ""
                    }
                ) {
                    Text("Cancelar", color = Color.White)
                }
            }
        )
    }

    if (showEditPasswordDialog && editingPassword != null) {
        AlertDialog(
            onDismissRequest = {
                showEditPasswordDialog = false
                editingPassword = null
                newPasswordTitle = ""
                newLoginValue = ""
                newPasswordValue = ""
                newPasswordCategory = categories.firstOrNull() ?: ""
            },
            title = { Text("Editar Senha", color = Color.White) },
            backgroundColor = Color(0xFF424242),
            text = {
                Column {
                    OutlinedTextField(
                        value = newPasswordTitle,
                        onValueChange = { newPasswordTitle = it },
                        label = { Text("Título", color = Color.White) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.White,
                            focusedBorderColor = Color.Yellow,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Yellow,
                            unfocusedLabelColor = Color.Gray,
                            cursorColor = Color.Yellow
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newLoginValue,
                        onValueChange = { newLoginValue = it },
                        label = { Text("Login", color = Color.White) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.White,
                            focusedBorderColor = Color.Yellow,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Yellow,
                            unfocusedLabelColor = Color.Gray,
                            cursorColor = Color.Yellow
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newPasswordValue,
                        onValueChange = { newPasswordValue = it },
                        label = { Text("Senha", color = Color.White) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.White,
                            focusedBorderColor = Color.Yellow,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Yellow,
                            unfocusedLabelColor = Color.Gray,
                            cursorColor = Color.Yellow
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = newPasswordCategory,
                            onValueChange = {},
                            label = { Text("Categoria", color = Color.White) },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Abrir menu",
                                    tint = Color.White
                                )
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = Color.White,
                                focusedBorderColor = Color.Yellow,
                                unfocusedBorderColor = Color.Gray,
                                focusedLabelColor = Color.Yellow,
                                unfocusedLabelColor = Color.Gray
                            )
                        )

                        DropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF424242)) // Fundo escuro
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    onClick = {
                                        newPasswordCategory = category
                                        categoryExpanded = false
                                    },
                                    modifier = Modifier.background(Color(0xFF424242)) // Fundo escuro
                                ) {
                                    Text(
                                        text = category,
                                        color = Color.White, // Texto branco
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .alpha(0f)
                                .clickable { categoryExpanded = true }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPasswordTitle.isBlank() || newPasswordValue.isBlank() || newLoginValue.isBlank()) {
                            Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val updatedItem = editingPassword!!.copy(
                            title = newPasswordTitle,
                            login = newLoginValue,
                            password = newPasswordValue,
                            category = newPasswordCategory
                        )

                        coroutineScope.launch {
                            try {
                                viewModel.updatePassword(updatedItem)
                                Toast.makeText(context, "Senha atualizada", Toast.LENGTH_SHORT).show()
                                showEditPasswordDialog = false
                                editingPassword = null
                                newPasswordTitle = ""
                                newLoginValue = ""
                                newPasswordValue = ""
                                newPasswordCategory = categories.firstOrNull() ?: ""
                            } catch (e: Exception) {
                                Toast.makeText(context, "Erro ao atualizar senha", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE2DA06))
                ) {
                    Text("Salvar", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showEditPasswordDialog = false
                        editingPassword = null
                        newPasswordTitle = ""
                        newLoginValue = ""
                        newPasswordValue = ""
                        newPasswordCategory = categories.firstOrNull() ?: ""
                    }
                ) {
                    Text("Cancelar", color = Color.White)
                }
            }
        )
    }

    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Nova Categoria", color = Color.White) },
            backgroundColor = Color(0xFF424242),
            text = {
                OutlinedTextField(
                    value = newCategoryValue,
                    onValueChange = { newCategoryValue = it },
                    label = { Text("Nome da Categoria", color = Color.White) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Color.White,
                        focusedBorderColor = Color.Yellow,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Color.Yellow,
                        unfocusedLabelColor = Color.Gray,
                        cursorColor = Color.Yellow
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryValue.isBlank()) {
                            Toast.makeText(context, "Digite um nome para a categoria", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        coroutineScope.launch {
                            try {
                                viewModel.addCategory(newCategoryValue)
                                Toast.makeText(context, "Categoria adicionada", Toast.LENGTH_SHORT).show()
                                newCategoryValue = ""
                                showAddCategoryDialog = false
                            } catch (e: Exception) {
                                Toast.makeText(context, "Erro ao adicionar categoria", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFE2DA06))
                ) {
                    Text("Salvar", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Cancelar", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun PasswordCard(
    item: PasswordItem,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 8.dp)
            .clickable { isExpanded = !isExpanded },
        backgroundColor = Color(0xFF252525),
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                            .width(1.dp)
                            .background(Color.Black)
                            .padding(horizontal = 8.dp)
                    )

                    Text(
                        text = if (showPassword) item.password else item.password.replace(Regex("."), "•"),
                        color = Color.White,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    )

                    // Adicionando o "olhinho" para mostrar/esconder a senha
                    IconButton(
                        onClick = { showPassword = !showPassword },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPassword) "Ocultar senha" else "Mostrar senha",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Row {
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

            if (isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 8.dp)
                ) {
                    Text(
                        text = "Login: ${item.login}",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Categoria: ${item.category}",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}