package com.example.superid.homepages

import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
    var newPasswordDescription by remember { mutableStateOf("") }
    var newPasswordCategory by remember { mutableStateOf(categories.firstOrNull() ?: "") }
    var newCategoryValue by remember { mutableStateOf("") }
    var categoryToDelete by remember { mutableStateOf("") }
    var itemToDelete by remember { mutableStateOf<PasswordItem?>(null) }

    var editingPassword by remember { mutableStateOf<PasswordItem?>(null) }
    var categoryExpanded by remember { mutableStateOf(false) }

    var isEmailVerified by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    val colors = androidx.compose.material3.MaterialTheme.colorScheme
    val typography = androidx.compose.material3.MaterialTheme.typography
    val isDarkTheme = isSystemInDarkTheme()

    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.reload()?.addOnCompleteListener { task ->
            isEmailVerified = task.isSuccessful && user.isEmailVerified
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirmation) {
        Dialog(
            onDismissRequest = { showDeleteConfirmation = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .background(colors.surface, shape = RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        "Confirmar Exclusão",
                        color = colors.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    val message = if (categoryToDelete.isNotEmpty()) {
                        "Tem certeza que deseja excluir a categoria '$categoryToDelete'?"
                    } else {
                        "Tem certeza que deseja excluir '${itemToDelete?.title}'?"
                    }

                    Text(message, color = colors.onBackground)

                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                showDeleteConfirmation = false
                                categoryToDelete = ""
                                itemToDelete = null
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Cancelar", color = colors.primary)
                        }

                        Button(
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
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = colors.primary),
                            shape = RoundedCornerShape(50.dp)
                        ) {
                            Text("Confirmar", color = Color.Black)
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(vertical = 24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        // Topo: logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.superid),
                contentDescription = "Logo SuperID",
                modifier = Modifier.height(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Divider(color = colors.secondary, thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))
        if (!isEmailVerified) {
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(
                        color =colors.error,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.dp,
                        color =colors.error,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .height(45.dp)
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
                        tint = colors.onBackground,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = "Valide seu email para usar todas as funções",
                        color = colors.onBackground,
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
                    backgroundColor = colors.primary,
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
                    backgroundColor = colors.primary,
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
        Divider(
            color = colors.secondary,
            thickness = 0.3.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        val grouped = passwords.groupBy { it.category.ifEmpty { "Sem Categoria" } }


        if (passwords.isEmpty()) {
            // Mostra mensagem quando não há senhas
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhuma senha cadastrada",
                    color = colors.secondary.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center
                )
            }
        } else {
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

                            if (category != "Sem Categoria" &&
                                category != "Sites Web" &&
                                category != "Aplicativos" &&
                                category != "Teclados de Acesso Físico"
                            ) {
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
                            color = colors.secondary,
                            thickness = 0.3.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }

    // Modal de Nova Senha
    if (showAddPasswordDialog) {
        Dialog(
            onDismissRequest = { showAddPasswordDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .height(400.dp)
                    .background(colors.background, shape = RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Título amarelo centralizado
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Nova Senha",
                            color = colors.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Restante do conteúdo do modal...
                    Spacer(Modifier.height(16.dp))

                    // Dropdown de categorias
                    Text("Categoria", color = colors.secondary)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = newPasswordCategory.ifEmpty { "Selecione uma categoria" },
                            color = if (newPasswordCategory.isEmpty()) colors.secondary else colors.onBackground,
                            modifier = Modifier
                                .clickable { categoryExpanded = true }
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )
                        IconButton(
                            onClick = { categoryExpanded = true },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown", tint = colors.onBackground)
                        }
                        DropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .background(colors.background)
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    onClick = {
                                        newPasswordCategory = category
                                        categoryExpanded = false
                                    },
                                    modifier = Modifier.background(colors.background)
                                ) {
                                    Text(
                                        text = category,
                                        color = colors.onBackground,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                    Divider(color = colors.primary, thickness = 1.dp)
                    Spacer(Modifier.height(8.dp))

                    // Campos do formulário
                    TextField(
                        value = newPasswordTitle,
                        onValueChange = { newPasswordTitle = it },
                        placeholder = { Text("Título", color = colors.secondary) },
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = colors.onBackground,
                            backgroundColor = Color.Transparent,
                            unfocusedIndicatorColor = colors.primary,
                            focusedIndicatorColor = colors.primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextField(
                        value = newLoginValue,
                        onValueChange = { newLoginValue = it },
                        placeholder = { Text("Login", color = colors.secondary) },
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = colors.onBackground,
                            backgroundColor = Color.Transparent,
                            unfocusedIndicatorColor = colors.primary,
                            focusedIndicatorColor = colors.primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextField(
                        value = newPasswordValue,
                        onValueChange = { newPasswordValue = it },
                        placeholder = { Text("Senha", color = colors.secondary) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = colors.onBackground,
                            backgroundColor = Color.Transparent,
                            unfocusedIndicatorColor = colors.primary,
                            focusedIndicatorColor = colors.primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextField(
                        value = newPasswordDescription,
                        onValueChange = { newPasswordDescription = it },
                        placeholder = { Text("Descrição", color = colors.secondary) },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = colors.onBackground,
                            backgroundColor = Color.Transparent,
                            unfocusedIndicatorColor = colors.primary,
                            focusedIndicatorColor = colors.primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

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
                                description = newPasswordDescription,
                                category = newPasswordCategory
                            )

                            coroutineScope.launch {
                                try {
                                    viewModel.addPassword(newItem)
                                    Toast.makeText(context, "Senha adicionada", Toast.LENGTH_SHORT).show()
                                    newPasswordTitle = ""
                                    newLoginValue = ""
                                    newPasswordValue = ""
                                    newPasswordDescription = ""
                                    newPasswordCategory = categories.firstOrNull() ?: ""
                                    showAddPasswordDialog = false
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Erro ao adicionar senha", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = colors.primary),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Criar", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Modal de Edição de Senha
    if (showEditPasswordDialog && editingPassword != null) {
        Dialog(
            onDismissRequest = {
                showEditPasswordDialog = false
                editingPassword = null
                newPasswordTitle = ""
                newLoginValue = ""
                newPasswordValue = ""
                newPasswordDescription = ""
                newPasswordCategory = categories.firstOrNull() ?: ""
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .height(550.dp)
                    .background(colors.surface, shape = RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Título amarelo centralizado
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Editar Senha",
                            color = colors.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Restante do conteúdo do modal...
                    Spacer(Modifier.height(16.dp))

                    // Dropdown de categorias
                    Text("Categoria", color = colors.secondary)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = newPasswordCategory.ifEmpty { "Selecione uma categoria" },
                            color = if (newPasswordCategory.isEmpty()) colors.secondary else colors.onBackground,
                            modifier = Modifier
                                .clickable { categoryExpanded = true }
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )
                        IconButton(
                            onClick = { categoryExpanded = true },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown", tint = colors.onBackground)
                        }
                        DropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .background(colors.surface)
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    onClick = {
                                        newPasswordCategory = category
                                        categoryExpanded = false
                                    },
                                    modifier = Modifier.background(colors.surface)
                                ) {
                                    Text(
                                        text = category,
                                        color = colors.onBackground,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                    Divider(color = colors.primary, thickness = 1.dp)
                    Spacer(Modifier.height(8.dp))

                    // Campos do formulário
                    TextField(
                        value = newPasswordTitle,
                        onValueChange = { newPasswordTitle = it },
                        placeholder = { Text("Título", color = colors.secondary) },
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = colors.onBackground,
                            backgroundColor = Color.Transparent,
                            unfocusedIndicatorColor = colors.primary,
                            focusedIndicatorColor = colors.primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextField(
                        value = newLoginValue,
                        onValueChange = { newLoginValue = it },
                        placeholder = { Text("Login", color = colors.secondary) },
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = colors.onBackground,
                            backgroundColor = Color.Transparent,
                            unfocusedIndicatorColor = colors.primary,
                            focusedIndicatorColor = colors.primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextField(
                        value = newPasswordValue,
                        onValueChange = { newPasswordValue = it },
                        placeholder = { Text("Senha", color = colors.secondary) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = colors.onBackground,
                            backgroundColor = Color.Transparent,
                            unfocusedIndicatorColor = colors.primary,
                            focusedIndicatorColor = colors.primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    TextField(
                        value = newPasswordDescription,
                        onValueChange = { newPasswordDescription = it },
                        placeholder = { Text("Descrição", color = colors.secondary) },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = colors.onBackground,
                            backgroundColor = Color.Transparent,
                            unfocusedIndicatorColor = colors.primary,
                            focusedIndicatorColor = colors.primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

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
                                description = newPasswordDescription,
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
                                    newPasswordDescription = ""
                                    newPasswordCategory = categories.firstOrNull() ?: ""
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Erro ao atualizar senha", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = colors.primary),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Salvar", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Modal de Nova Categoria
    if (showAddCategoryDialog) {
        Dialog(
            onDismissRequest = { showAddCategoryDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .background(colors.background, shape = RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Título amarelo centralizado
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Nova Categoria",
                            color = colors.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    // Campo de texto
                    TextField(
                        value = newCategoryValue,
                        onValueChange = { newCategoryValue = it },
                        placeholder = { Text("Nome da categoria", color = colors.secondary) },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = colors.onBackground,
                            backgroundColor = Color.Transparent,
                            unfocusedIndicatorColor = colors.primary,
                            focusedIndicatorColor = colors.primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botões
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showAddCategoryDialog = false },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Cancelar", color = colors.primary)
                        }

                        Button(
                            onClick = {
                                if (newCategoryValue.isBlank()) {
                                    Toast.makeText(context, "Digite um nome para a categoria", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                coroutineScope.launch {
                                    try {
                                        viewModel.addCategory(newCategoryValue)
                                        Toast.makeText(context, "Categoria criada!", Toast.LENGTH_SHORT).show()
                                        newCategoryValue = ""
                                        showAddCategoryDialog = false
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                                        Log.e("PasswordPage", "Erro ao adicionar categoria", e)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = colors.primary),
                            shape = RoundedCornerShape(50.dp)
                        ) {
                            Text("Salvar", color = Color.Black)
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
    var isExpanded by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }

    val colors = androidx.compose.material3.MaterialTheme.colorScheme
    val typography = androidx.compose.material3.MaterialTheme.typography
    val isDarkTheme = isSystemInDarkTheme()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 8.dp)
            .clickable { isExpanded = !isExpanded },
        backgroundColor = colors.background,
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
                        color = colors.onBackground,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                            .background(colors.onPrimary)
                            .padding(horizontal = 8.dp)
                    )

                    Text(
                        text = if (showPassword) item.password else item.password.replace(Regex("."), "•"),
                        color = colors.onBackground,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    )

                    IconButton(
                        onClick = { showPassword = !showPassword },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (showPassword) "Ocultar senha" else "Mostrar senha",
                            tint = colors.onBackground.copy(alpha = 0.7f)
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
                        color = colors.secondary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Descrição: ${item.description}",
                        color = colors.secondary,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}