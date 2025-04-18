package com.example.superid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var termsError by remember { mutableStateOf("") }

    val auth: FirebaseAuth = Firebase.auth
    val context = LocalContext.current

    val yellow = Color(0xFFE2DA06)
    val darkYellow = Color(0xFFC3BC00)
    val background = Color(0xFF131313)
    val white = Color(0xFFFFFFFF)
    val grayText = Color(0xFFAFAFAF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .padding(horizontal = 32.dp, vertical = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Image(
            painter = painterResource(id = R.drawable.superidlogowhiteyellow),
            contentDescription = "Logo SuperID",
            modifier = Modifier
                .height(24.dp)
                .offset { IntOffset(x = 0, y = 100) } // Sobe o logo 180 pixels
        )
        Spacer(modifier = Modifier.height(100.dp)) // Espaço antes do ícone

        Text(
            text = "Você é novo?",
            fontSize = 18.sp,
            color = grayText,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Vamos te cadastrar!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = white,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text("Nome completo:", color = grayText, fontSize = 14.sp)
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                nameError = ""
            },
            placeholder = { Text("Digite seu nome completo", color = grayText) },
            isError = nameError.isNotEmpty(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = white,
                unfocusedTextColor = white,
                cursorColor = white,
                focusedBorderColor = darkYellow,
                unfocusedBorderColor = yellow
            )
        )
        if (nameError.isNotEmpty()) {
            Text(nameError, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Seu melhor e-mail:", color = grayText, fontSize = 14.sp)
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = ""
            },
            placeholder = { Text("exemplo@email.com", color = grayText) },
            isError = emailError.isNotEmpty(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = white,
                unfocusedTextColor = white,
                cursorColor = white,
                focusedBorderColor = darkYellow,
                unfocusedBorderColor = yellow
            )
        )
        if (emailError.isNotEmpty()) {
            Text(emailError, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Sua melhor senha:", color = grayText, fontSize = 14.sp)
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = ""
            },
            placeholder = { Text("Digite sua senha", color = grayText) },
            visualTransformation = PasswordVisualTransformation(),
            isError = passwordError.isNotEmpty(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = white,
                unfocusedTextColor = white,
                cursorColor = white,
                focusedBorderColor = darkYellow,
                unfocusedBorderColor = yellow
            )
        )
        if (passwordError.isNotEmpty()) {
            Text(passwordError, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Divider(
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 1.dp,
            color = grayText.copy(alpha = 0.3f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = termsAccepted,
                onCheckedChange = {
                    termsAccepted = it
                    termsError = ""
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = yellow,
                    uncheckedColor = white
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            ClickableText(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = grayText)) {
                        append("Eu li e concordo com os ")
                    }
                    withStyle(
                        SpanStyle(
                            color = grayText,
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append("Termos de Uso e Privacidade")
                    }
                },
                onClick = { /* abrir termos */ }
            )
        }
        if (termsError.isNotEmpty()) {
            Text(termsError, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

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
                                context.startActivity(Intent(context, HomeActivity::class.java))
                            } else {
                                errorMessage = task.exception?.message ?: "Erro desconhecido"
                            }
                        }
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = yellow,
                contentColor = background
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = background,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text("Cadastrar", fontWeight = FontWeight.Bold)
            }
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}


private fun validateForm(
    name: String,
    email: String,
    password: String,
    termsAccepted: Boolean
): Boolean {
    if (name.isEmpty()) return false
    if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) return false
    if (password.length < 6) return false
    if (!termsAccepted) return false
    return true
}
