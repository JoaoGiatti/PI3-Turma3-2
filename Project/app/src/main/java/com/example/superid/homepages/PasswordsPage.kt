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
import com.google.common.collect.Multimaps.index
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

@Composable
fun PasswordPage(navController: NavController, viewModel: PasswordViewModel = viewModel()) {
    val context = LocalContext.current
    val passwords by remember { derivedStateOf { viewModel.passwords } }

    var showAddPasswordDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    var newPasswordTitle by remember { mutableStateOf("") }
    var newPasswordValue by remember { mutableStateOf("") }
    var newPasswordCategory by remember { mutableStateOf("") }
    var newCategoryValue by remember { mutableStateOf("") }

    val colors = MaterialTheme.colors
    val typography = MaterialTheme.typography

    // Estado para verificar se o e-mail está verificado
    var isEmailVerified by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    // Verificar o status de verificação do e-mail
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
        // Topo: logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp), // opcional
            contentAlignment = Alignment.TopCenter
        ) {
            val imageResLogo = R.drawable.superid
            Image(
                painter = painterResource(id = imageResLogo),
                contentDescription = "Logo SuperID",
                modifier = Modifier.height(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Alerta de verificação de e-mail
        if (!isEmailVerified) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color.Black)
                    .border(1.dp, Color.Red, RoundedCornerShape(4.dp))
                    .height(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Alerta: valide o email para usar todas as funções",
                    color = Color.Red,
                    style = typography.subtitle2,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Botões
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

        Divider(
            color = Color.Gray,
            thickness = 0.3.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        val grouped = passwords.groupBy { it.category.ifEmpty { "Sem Categoria" } }

        LazyColumn {
            grouped.forEach { (category, items) ->
                item {
                    Text(
                        text = category,
                        color = Color(0xFF555555),
                        style = typography.subtitle1,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                items(items) { item ->
                    PasswordCard(item, onDelete = {
                        viewModel.deletePassword(item)
                        Toast.makeText(context, "Senha removida", Toast.LENGTH_SHORT).show()
                    }, onEdit = {
                        Toast.makeText(context, "Editar ainda não implementado", Toast.LENGTH_SHORT).show()
                    })
                }

                // Divider após o último card de cada categoria
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

    // Dialog Nova Senha
    if (showAddPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showAddPasswordDialog = false },
            title = { Text("Adicionar Nova Senha") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newPasswordTitle,
                        onValueChange = { newPasswordTitle = it },
                        label = { Text("Título") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPasswordValue,
                        onValueChange = { newPasswordValue = it },
                        label = { Text("Senha") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPasswordCategory,
                        onValueChange = { newPasswordCategory = it },
                        label = { Text("Categoria") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val newItem = PasswordItem(
                        title = newPasswordTitle,
                        password = newPasswordValue,
                        category = newPasswordCategory
                    )
                    viewModel.addPassword(newItem)
                    newPasswordTitle = ""
                    newPasswordValue = ""
                    newPasswordCategory = ""
                    showAddPasswordDialog = false
                }) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPasswordDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Dialog Nova Categoria
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Nova Categoria") },
            text = {
                OutlinedTextField(
                    value = newCategoryValue,
                    onValueChange = { newCategoryValue = it },
                    label = { Text("Nome da Categoria") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    Toast.makeText(context, "Categoria adicionada: $newCategoryValue", Toast.LENGTH_SHORT).show()
                    newCategoryValue = ""
                    showAddCategoryDialog = false
                }) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Cancelar")
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
                        .background(Color.Black) // divisória preta
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
                    painter = painterResource(id = R.drawable.btn_edit),  // Substituindo pelo ícone de edição
                    contentDescription = "Editar",
                    modifier = Modifier.size(34.dp)
                )
            }
            // Substituindo o ícone de Excluir por uma imagem personalizada
            IconButton(onClick = onDelete) {
                Image(
                    painter = painterResource(id = R.drawable.btn_exclude),  // Substituindo pelo ícone de exclusão
                    contentDescription = "Deletar",
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}


