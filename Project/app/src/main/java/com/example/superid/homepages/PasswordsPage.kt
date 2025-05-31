package com.example.superid.homepages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.input.ImeAction
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
    var newPasswordUrl by remember { mutableStateOf("") }
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
    val auth = FirebaseAuth.getInstance()
    var emailVerified by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val typography = androidx.compose.material3.MaterialTheme.typography

    fun resendVerificationEmail() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(context, "Erro: Usuário não encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        user.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        context,
                        "Email de verificação enviado para ${user.email}",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val error = task.exception?.message ?: "Erro desconhecido"
                    val message = when {
                        error.contains("network", true) -> "Falha de rede. Verifique sua conexão"


                        else -> "Falha ao enviar: Muitas tentativas seguidas. Aguarde cerca de 1 minuto antes de tentar novamente."
                    }
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }
    }
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
                    .background(Color(0xFF1C1C1E), shape = RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        "Confirmar Exclusão",
                        color = Color(0xFFFFFF00),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    val message = if (categoryToDelete.isNotEmpty()) {
                        "Tem certeza que deseja excluir a categoria '$categoryToDelete' e TODAS as senhas nela contidas?"
                    } else {
                        "Tem certeza que deseja excluir '${itemToDelete?.title}'?"
                    }

                    Text(message, color = Color.White)

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
                            Text("Cancelar", color = Color(0xFFFFFF00))
                        }

                        Button(
                            onClick = {
                                showDeleteConfirmation = false
                                coroutineScope.launch {
                                    try {
                                        if (categoryToDelete.isNotEmpty()) {
                                            // Excluir todas as senhas dessa categoria
                                            val passwordsToDelete =
                                                passwords.filter { it.category == categoryToDelete }
                                            passwordsToDelete.forEach { viewModel.deletePassword(it) }

                                            // Excluir a categoria
                                            viewModel.deleteCategory(categoryToDelete)
                                            categoryToDelete = ""
                                        } else {
                                            itemToDelete?.let {
                                                viewModel.deletePassword(it)
                                                itemToDelete = null
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Erro ao excluir: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFFF00)),
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
        Divider(color = Color.Gray, thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))
        if (!isEmailVerified) {
            Spacer(modifier = Modifier.height(12.dp))

            // Botão para reenviar email de verificação
            Button(
                onClick = { resendVerificationEmail() },
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(40.dp)
            ) {
                Row(
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
                        text = "Clique para reenviar o email de verificação",
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
                Text("Nova Senha", color = Color.Black,  style = typography.labelMedium)
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
                Text("Nova Categoria", color = Color.Black,  style = typography.labelMedium)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        Divider(
            color = Color.Gray,
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
                    color = Color.Gray.copy(alpha = 0.7f),
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
                                color = Color(0xFFA1A1A1),
                                style = typography.labelMedium,
                            )

                            Spacer(modifier = Modifier.weight(1f))

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
                                    modifier = Modifier
                                        .size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Excluir categoria",
                                        tint = Color(0xFFA1A1A1),
                                        modifier = Modifier.size(20.dp)
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
                                newPasswordDescription = item.description
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
    }

    // Modal de Nova Senha
    if (showAddPasswordDialog) {
        var passwordVisible by remember { mutableStateOf(false) }

        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        val coroutineScope = rememberCoroutineScope()

        // Configuração para ajustar o layout quando o teclado aparece
        val imePadding = WindowInsets.ime.asPaddingValues()

        Dialog(
            onDismissRequest = { showAddPasswordDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier

                    .padding(horizontal = 44.dp)
                    .imePadding()
                    .verticalScroll(scrollState),// Adiciona padding para o teclado
            ) {
                Column(
                    modifier = Modifier

                        .nestedScroll(rememberNestedScrollInteropConnection())// Comportamento otimizado para teclado
                        .background(Color(0xFF1C1C1E), shape = RoundedCornerShape(20.dp))
                        .padding(20.dp)

                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center

                    ) {
                        Text(
                            "Nova Senha",
                            color = Color(0xFFFFFF00),
                            style = typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    Text("Categoria", color = Color.Gray, style = typography.labelMedium)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = newPasswordCategory.ifEmpty { "Selecione uma categoria" },
                            color = if (newPasswordCategory.isEmpty()) Color.Gray else Color.White,
                            modifier = Modifier
                                .clickable { categoryExpanded = true }
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        )
                        IconButton(
                            onClick = { categoryExpanded = true },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = Color.White
                            )
                        }
                        DropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false },
                            modifier = Modifier
                                .width(300.dp)
                                .background(Color(0xFF2E2E2E))
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    onClick = {
                                        newPasswordCategory = category
                                        categoryExpanded = false
                                    },
                                    modifier = Modifier.background(Color(0xFF2E2E2E))
                                ) {
                                    Text(
                                        text = category,
                                        color = Color.White,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                    Divider(color = Color(0xFFFFFF00), thickness = 1.dp)
                    Spacer(Modifier.height(8.dp))

                    // Campos do formulário com onFocusChanged
                    Column(modifier = Modifier.padding(bottom = imePadding.calculateBottomPadding())) {
                        TextField(
                            value = newPasswordTitle,
                            onValueChange = { newPasswordTitle = it },
                            placeholder = { Text("Título*", color = Color.Gray, style = typography.labelMedium) },
                            singleLine = true,
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.White,
                                backgroundColor = Color.Transparent,
                                unfocusedIndicatorColor = Color(0xFFFFFF00),
                                focusedIndicatorColor = Color(0xFFFFFF00),
                                cursorColor = Color(0xFFFFFF00)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    if (focusState.isFocused) {
                                        coroutineScope.launch {
                                            scrollState.scrollTo(scrollState.maxValue)
                                        }
                                    }
                                }
                        )

                        Spacer(Modifier.height(12.dp))

                        TextField(
                            value = newLoginValue,
                            onValueChange = { newLoginValue = it },
                            placeholder = { Text("Login*", color = Color.Gray, style = typography.labelMedium) },
                            singleLine = true,
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.White,
                                backgroundColor = Color.Transparent,
                                unfocusedIndicatorColor = Color(0xFFFFFF00),
                                focusedIndicatorColor = Color(0xFFFFFF00),
                                cursorColor = Color(0xFFFFFF00)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    if (focusState.isFocused) {
                                        coroutineScope.launch {
                                            scrollState.scrollTo(scrollState.maxValue)
                                        }
                                    }
                                }
                        )

                        Spacer(Modifier.height(12.dp))

                        TextField(
                            value = newPasswordValue,
                            onValueChange = { newPasswordValue = it },
                            placeholder = { Text("Senha*", color = Color.Gray,style = typography.labelMedium) },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Ocultar senha" else "Mostrar senha",
                                        tint = Color(0xFFFFFF00)
                                    )
                                }
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.White,
                                backgroundColor = Color.Transparent,
                                unfocusedIndicatorColor = Color(0xFFFFFF00),
                                focusedIndicatorColor = Color(0xFFFFFF00),
                                cursorColor = Color(0xFFFFFF00)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    if (focusState.isFocused) {
                                        coroutineScope.launch {
                                            scrollState.scrollTo(scrollState.maxValue)
                                        }
                                    }
                                }
                        )

                        Spacer(Modifier.height(12.dp))

                        TextField(
                            value = newPasswordDescription,
                            onValueChange = {
                                // Limita a descrição a 100 caracteres
                                if (it.length <= 100) {
                                    newPasswordDescription = it
                                }
                            },
                            placeholder = { Text("Descrição ", color = Color.Gray, style = typography.labelMedium) },
                            singleLine = false, // Permite múltiplas linhas para visualização
                            maxLines = 3, // Limita a 3 linhas visíveis
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next // Faz o Enter do teclado ir para o próximo campo
                            ),

                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.White,
                                backgroundColor = Color.Transparent,
                                unfocusedIndicatorColor = Color(0xFFFFFF00),
                                focusedIndicatorColor = Color(0xFFFFFF00),
                                cursorColor = Color(0xFFFFFF00)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    if (focusState.isFocused) {
                                        coroutineScope.launch {
                                            scrollState.scrollTo(scrollState.maxValue)
                                        }
                                    }
                                }
                        )

// Adicione este texto abaixo do campo para mostrar o contador de caracteres
                        Text(
                            text = "${newPasswordDescription.length}/100",
                            color = if (newPasswordDescription.length == 100) Color.Red else Color.Gray,
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.align(Alignment.End)
                        )

                        Spacer(Modifier.height(8.dp))

                        TextField(
                            value = newPasswordUrl,
                            onValueChange = { newPasswordUrl = it },
                            placeholder = { Text("URL do site", color = Color.Gray, style = typography.labelMedium) },
                            singleLine = true,
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.White,
                                backgroundColor = Color.Transparent,
                                unfocusedIndicatorColor = Color(0xFFFFFF00),
                                focusedIndicatorColor = Color(0xFFFFFF00),
                                cursorColor = Color(0xFFFFFF00)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    if (focusState.isFocused) {
                                        coroutineScope.launch {
                                            scrollState.scrollTo(scrollState.maxValue)
                                        }
                                    }
                                }
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            keyboardController?.hide()
                            focusManager.clearFocus()

                            if (newPasswordTitle.isBlank() || newPasswordValue.isBlank() || newLoginValue.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Preencha todos os campos obrigatórios",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            fun generateAccessToken(length: Int = 256): String {
                                val charset =
                                    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
                                return (1..length)
                                    .map { charset.random() }
                                    .joinToString("")
                            }

                            val newItem = PasswordItem(
                                title = newPasswordTitle,
                                login = newLoginValue,
                                password = newPasswordValue,
                                description = newPasswordDescription,
                                category = newPasswordCategory,
                                url = newPasswordUrl,
                                accessToken = generateAccessToken()
                            )

                            coroutineScope.launch {
                                try {
                                    viewModel.addPassword(newItem)
                                    Toast.makeText(context, "Senha adicionada", Toast.LENGTH_SHORT)
                                        .show()
                                    newPasswordTitle = ""
                                    newLoginValue = ""
                                    newPasswordUrl = ""
                                    newPasswordValue = ""
                                    newPasswordDescription = ""
                                    newPasswordCategory = categories.firstOrNull() ?: ""
                                    showAddPasswordDialog = false
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Erro ao adicionar senha",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFFF00)),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding() // Padding para a barra de navegação
                    ) {
                        Text("Criar", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

// Modal de Edição de Senha - Atualizado com as mesmas melhorias
    if (showEditPasswordDialog && editingPassword != null) {
        var passwordVisible by remember { mutableStateOf(false) }
        val scrollState = rememberScrollState()
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        val coroutineScope = rememberCoroutineScope()

        // Configuração para ajustar o layout quando o teclado aparece
        val imePadding = WindowInsets.ime.asPaddingValues()

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
                    .padding(horizontal = 44.dp)
                    .imePadding() // Adiciona padding para o teclado
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .nestedScroll(rememberNestedScrollInteropConnection())// Comportamento otimizado para teclado
                        .background(Color(0xFF1C1C1E), shape = RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Editar Senha",
                            color = Color(0xFFFFFF00),
                            style = typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    Text("Categoria", color = Color.Gray, style = typography.labelMedium)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = newPasswordCategory.ifEmpty { "Selecione uma categoria*" },
                            color = if (newPasswordCategory.isEmpty()) Color.Gray else Color.White,
                            modifier = Modifier
                                .clickable { categoryExpanded = true }

                                .padding(vertical = 16.dp)
                        )
                        IconButton(
                            onClick = { categoryExpanded = true },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = Color.White
                            )
                        }
                        DropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false },
                            modifier = Modifier
                                .width(300.dp)
                                .background(Color(0xFF2E2E2E))
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    onClick = {
                                        newPasswordCategory = category
                                        categoryExpanded = false
                                    },
                                    modifier = Modifier.background(Color(0xFF2E2E2E))
                                ) {
                                    Text(
                                        text = category,
                                        color = Color.White,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                    Divider(color = Color(0xFFFFFF00), thickness = 1.dp)
                    Spacer(Modifier.height(8.dp))

                    // Campos do formulário com onFocusChanged
                    Column(modifier = Modifier.padding(bottom = imePadding.calculateBottomPadding())) {
                        TextField(
                            value = newPasswordTitle,
                            onValueChange = { newPasswordTitle = it },
                            placeholder = { Text("Título*", color = Color.Gray, style = typography.labelMedium) },
                            singleLine = true,
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.White,
                                backgroundColor = Color.Transparent,
                                unfocusedIndicatorColor = Color(0xFFFFFF00),
                                focusedIndicatorColor = Color(0xFFFFFF00),
                                cursorColor = Color(0xFFFFFF00)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    if (focusState.isFocused) {
                                        coroutineScope.launch {
                                            scrollState.scrollTo(scrollState.maxValue)
                                        }
                                    }
                                }
                        )

                        Spacer(Modifier.height(12.dp))

                        TextField(
                            value = newLoginValue,
                            onValueChange = { newLoginValue = it },
                            placeholder = { Text("Login*", color = Color.Gray, style = typography.labelMedium) },
                            singleLine = true,
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.White,
                                backgroundColor = Color.Transparent,
                                unfocusedIndicatorColor = Color(0xFFFFFF00),
                                focusedIndicatorColor = Color(0xFFFFFF00),
                                cursorColor = Color(0xFFFFFF00)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    if (focusState.isFocused) {
                                        coroutineScope.launch {
                                            scrollState.scrollTo(scrollState.maxValue)
                                        }
                                    }
                                }
                        )

                        Spacer(Modifier.height(12.dp))

                        TextField(
                            value = newPasswordValue,
                            onValueChange = { newPasswordValue = it },
                            placeholder = { Text("Senha*", color = Color.Gray, style = typography.labelMedium) },
                            singleLine = true,
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Ocultar senha" else "Mostrar senha",
                                        tint = Color(0xFFFFFF00)
                                    )
                                }
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.White,
                                backgroundColor = Color.Transparent,
                                unfocusedIndicatorColor = Color(0xFFFFFF00),
                                focusedIndicatorColor = Color(0xFFFFFF00),
                                cursorColor = Color(0xFFFFFF00)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    if (focusState.isFocused) {
                                        coroutineScope.launch {
                                            scrollState.scrollTo(scrollState.maxValue)
                                        }
                                    }
                                }
                        )

                        Spacer(Modifier.height(12.dp))

                        TextField(
                            value = newPasswordDescription,
                            onValueChange = {
                                // Limita a descrição a 100 caracteres
                                if (it.length <= 100) {
                                    newPasswordDescription = it
                                }
                            },
                            placeholder = { Text("Descrição", color = Color.Gray, style = typography.labelMedium) },
                            singleLine = false, // Permite múltiplas linhas para visualização
                            maxLines = 3, // Limita a 3 linhas visíveis


                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.White,
                                backgroundColor = Color.Transparent,
                                unfocusedIndicatorColor = Color(0xFFFFFF00),
                                focusedIndicatorColor = Color(0xFFFFFF00),
                                cursorColor = Color(0xFFFFFF00)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    if (focusState.isFocused) {
                                        coroutineScope.launch {
                                            scrollState.scrollTo(scrollState.maxValue)
                                        }
                                    }
                                }
                        )
                    }

                        Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = {
                            keyboardController?.hide()
                            focusManager.clearFocus()

                            if (newPasswordTitle.isBlank() || newPasswordValue.isBlank() || newLoginValue.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Preencha todos os campos",
                                    Toast.LENGTH_SHORT
                                ).show()
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
                                    Toast.makeText(context, "Senha atualizada", Toast.LENGTH_SHORT)
                                        .show()
                                    showEditPasswordDialog = false
                                    editingPassword = null
                                    newPasswordTitle = ""
                                    newLoginValue = ""
                                    newPasswordValue = ""
                                    newPasswordDescription = ""
                                    newPasswordCategory = categories.firstOrNull() ?: ""
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Erro ao atualizar senha",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFFF00)),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding() // Padding para a barra de navegação
                    ) {
                        Text("Salvar", color = Color.Black, style = typography.labelMedium)
                    }
                }
            }
        }
    }

    // Modal de Nova Categoria - CORRIGIDO
    if (showAddCategoryDialog) {
        val scrollState = rememberScrollState()

        Dialog(
            onDismissRequest = { showAddCategoryDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 44.dp)
                    .imePadding() // Adiciona padding para o teclado
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .background(Color(0xFF1C1C1E), shape = RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Nova Categoria",
                            color = Color(0xFFFFFF00),
                            style = typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    TextField(
                        value = newCategoryValue,
                        onValueChange = { newCategoryValue = it },
                        placeholder = { Text("Nome da categoria*", color = Color.Gray, style = typography.labelMedium) },
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.White,
                            backgroundColor = Color.Transparent,
                            unfocusedIndicatorColor = Color(0xFFFFFF00),
                            focusedIndicatorColor = Color(0xFFFFFF00),
                            cursorColor = Color(0xFFFFFF00)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showAddCategoryDialog = false },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Cancelar", color = Color(0xFFFFFF00), style = typography.labelMedium)
                        }

                        Button(
                            onClick = {
                                if (newCategoryValue.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        "Digite um nome para a categoria",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }
                                coroutineScope.launch {
                                    try {
                                        viewModel.addCategory(newCategoryValue)
                                        Toast.makeText(
                                            context,
                                            "Categoria criada!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        newCategoryValue = ""
                                        showAddCategoryDialog = false
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Erro: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.e("PasswordPage", "Erro ao adicionar categoria", e)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFFFF00)),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier.navigationBarsPadding() // Padding para a barra de navegação
                        ) {
                            Text("Salvar", color = Color.Black, style = typography.labelMedium)
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
                        text = "Descrição: ${item.description}",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}