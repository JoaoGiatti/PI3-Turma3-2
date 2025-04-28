package com.example.superid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import android.provider.Settings

import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignInScreen()
        }
    }
}

@Composable
fun SignInScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var termsError by remember { mutableStateOf("") }

    var showAlertDialog by remember { mutableStateOf(false) }

    val yellow = Color(0xFFE2DA06)
    val darkGray = Color(0xFF131313)
    val textWhite = Color(0xFFFFFFFF)
    val textGray = Color(0xFFAFAFAF)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = darkGray
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // Ícone de voltar
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Voltar",
                tint = textWhite,
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        context.startActivity(Intent(context, MainActivity::class.java))
                    }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.superidlogowhiteyellow),
                    contentDescription = "Logo Super ID",
                    modifier = Modifier.size(120.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text("Você é novo?", color = textWhite, fontSize = 16.sp)
            Text("Vamos te cadastrar!", color = textWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(24.dp))

            // Nome
            Text("Nome completo:", color = textWhite)
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = ""
                },
                placeholder = { Text("Seu nome completo", color = textGray) },
                isError = nameError.isNotEmpty(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = yellow,
                    unfocusedBorderColor = yellow,
                    focusedTextColor = textWhite,
                    unfocusedTextColor = textWhite,
                    cursorColor = yellow
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (nameError.isNotEmpty()) {
                Text(nameError, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            Text("Seu melhor e-mail:", color = textWhite)
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = ""
                },
                placeholder = { Text("exemplo@email.com", color = textGray) },
                isError = emailError.isNotEmpty(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = yellow,
                    unfocusedBorderColor = yellow,
                    focusedTextColor = textWhite,
                    unfocusedTextColor = textWhite,
                    cursorColor = yellow
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (emailError.isNotEmpty()) {
                Text(emailError, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Senha
            Text("Crie uma senha segura:", color = textWhite)
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = ""
                },
                placeholder = { Text("***********", color = textGray) },
                isError = passwordError.isNotEmpty(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(icon, contentDescription = null, tint = yellow)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = yellow,
                    unfocusedBorderColor = yellow,
                    focusedTextColor = textWhite,
                    unfocusedTextColor = textWhite,
                    cursorColor = yellow
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (passwordError.isNotEmpty()) {
                Text(passwordError, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(1.dp))

            // Aceite de termos
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = {
                        termsAccepted = it
                        termsError = ""
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = yellow,
                        uncheckedColor = textWhite
                    )
                )

                Text(
                    text = "Li e aceito os termos de uso e privacidade",
                    color = Color.White, // ou textWhite se for uma variável definida
                    modifier = Modifier.clickable {
                        val intent = Intent(context, TermsActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }
            if (termsError.isNotEmpty()) {
                Text(termsError, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botão de cadastro
            Button(
                onClick = {
                    var isValid = true
                    if (name.isBlank()) {
                        nameError = "Nome obrigatório"
                        isValid = false
                    }
                    if (email.isBlank()) {
                        emailError = "Email obrigatório"
                        isValid = false
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = "Email inválido"
                        isValid = false
                    }
                    if (password.length < 6) {
                        passwordError = "Senha deve ter no mínimo 6 caracteres"
                        isValid = false
                    }
                    if (!termsAccepted) {
                        termsError = "Você precisa aceitar os termos"
                        isValid = false
                    }

                    if (isValid) {
                        isLoading = true
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {

                                    // Envia email de verificação
                                    val user = auth.currentUser
                                    user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                                        if (verifyTask.isSuccessful) {
                                            // Vai para a tela de aviso de verificação de e-mail
                                            context.startActivity(Intent(context, EmailVerificationActivity::class.java))
                                            // Finaliza a tela de cadastro
                                            (context as? ComponentActivity)?.finish()
                                        } else {
                                            errorMessage = verifyTask.exception?.message ?: "Erro ao enviar email de verificação"
                                        }
                                    }

                                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                                    val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

                                    val userData = hashMapOf(
                                        "UID" to uid,
                                        "IMEI" to androidId,
                                        "emailMestre" to email,
                                        "nome" to name,
                                        "senhaMestre" to password
                                    )

                                    firestore.collection("Users")
                                        .document(uid)
                                        .set(userData)
                                        .addOnSuccessListener {
                                            context.startActivity(
                                                Intent(
                                                    context,
                                                    EmailVerificationActivity::class.java
                                                )
                                            )
                                        }
                                        .addOnFailureListener { e ->
                                            errorMessage =
                                                "Erro ao salvar dados: ${e.localizedMessage}"
                                        }

                                } else {
                                    errorMessage = task.exception?.message ?: "Erro desconhecido"
                                }
                            }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = yellow),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = darkGray,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Cadastrar", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            // Erro geral
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}