package com.example.superid

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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

    val yellow = Color(0xFFE2DA06)
    val darkGray = Color(0xFF131313)
    val textWhite = Color.White
    val textGray = Color(0xFFAFAFAF)
    val scrollState = rememberScrollState()

    // Ajuste no Surface para eliminar padding extra
    Surface(
        modifier = Modifier
            .fillMaxSize(), // Preencher toda a tela
        color = darkGray
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(50.dp) // Garantir que a Column ocupe toda a tela
                .verticalScroll(scrollState), // Habilitar rolagem vertical
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(10.dp)) // Ajuste do espaçamento superior

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.arrowback),
                    contentDescription = "Voltar",
                    modifier = Modifier
                        .size(38.dp)
                        .clickable {
                            (context as? ComponentActivity)?.finish()
                        }
                )
                Spacer(modifier = Modifier.width(72.dp))
                Image(
                    painter = painterResource(id = R.drawable.superidlogowhiteyellow),
                    contentDescription = "Logo SuperID",
                    modifier = Modifier.height(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(72.dp))

            Text("Você é novo?", color = textWhite, fontSize = 16.sp, fontFamily = FontFamily(Font(R.font.interregular)))
            Text("Vamos te cadastrar!", color = textWhite, fontSize = 24.sp, fontFamily = FontFamily(Font(R.font.poppinsbold)))

            Spacer(modifier = Modifier.height(24.dp))

            Text("Nome completo:", color = textWhite, fontFamily = FontFamily(Font(R.font.interbold)))
            Spacer(modifier = Modifier.height(10.dp))
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
            if (nameError.isNotEmpty()) Text(nameError, color = Color.Red, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(18.dp))

            Text("Seu melhor e-mail:", color = textWhite, fontFamily = FontFamily(Font(R.font.interbold)))
            Spacer(modifier = Modifier.height(10.dp))
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
            if (emailError.isNotEmpty()) Text(emailError, color = Color.Red, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Crie uma senha segura:", color = textWhite, fontFamily = FontFamily(Font(R.font.interbold)))
            Spacer(modifier = Modifier.height(10.dp))
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
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = yellow
                        )
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
            if (passwordError.isNotEmpty()) Text(passwordError, color = Color.Red, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = termsAccepted,
                    onCheckedChange = {
                        termsAccepted = it
                        termsError = ""
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = yellow,
                        uncheckedColor = yellow
                    )
                )

                val annotatedString = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White)) { append("Li e aceito os ") }
                    withStyle(style = SpanStyle(color = textGray, textDecoration = TextDecoration.Underline)) {
                        append("Termos de uso e Privacidade")
                    }
                }

                Text(
                    text = annotatedString,

                    )
            }
            if (termsError.isNotEmpty()) Text(termsError, color = Color.Red, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(14.dp))

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
                                    val user = auth.currentUser
                                    user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                                        if (verifyTask.isSuccessful) {
                                            Log.d("SignInActivity", "Email de verificação enviado com sucesso")
                                            context.startActivity(Intent(context, EmailVerificationActivity::class.java))
                                            (context as? ComponentActivity)?.finish()
                                        } else {
                                            errorMessage = verifyTask.exception?.message ?: "Erro ao enviar email de verificação"
                                        }
                                    }
                                } else {
                                    errorMessage = task.exception?.message ?: "Erro desconhecido"
                                    Log.e("Auth", "Erro ao criar usuário", task.exception)
                                }
                            }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = yellow),
                modifier = Modifier.fillMaxWidth().height(56.dp),
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

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}
