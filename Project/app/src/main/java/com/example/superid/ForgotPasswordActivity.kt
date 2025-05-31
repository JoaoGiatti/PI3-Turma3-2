package com.example.superid

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.ui.theme.SuperIDTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Recebe o parâmetro que indica se veio da tela de login
        val fromLogin = intent.getBooleanExtra("fromLogin", true)
        setContent {
            SuperIDTheme {
                ForgotPasswordScreen(fromLogin = fromLogin)
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen(fromLogin: Boolean) {
    val context = LocalContext.current
    val auth: FirebaseAuth = Firebase.auth
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.superidlogowhiteyellow),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Redefinir Senha",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Digite seu email para receber o link de redefinição",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de email com ícone
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = "Email")
                },
                label = { Text("Email") },
                isError = emailError,
                supportingText = {
                    if (emailError) Text("Email inválido", color = MaterialTheme.colorScheme.error)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    
                    if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = true
                        return@Button
                    }

                    isLoading = true
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "Email enviado! Verifique sua caixa de entrada.",
                                    Toast.LENGTH_LONG
                                ).show()
                                (context as? ComponentActivity)?.finish()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Erro: ${task.exception?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("Enviar Link de Redefinição")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Voltar - Comportamento inteligente
            OutlinedButton(
                onClick = { (context as? ComponentActivity)?.finish() },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = colors.onBackground
                ),
                border = ButtonDefaults.outlinedButtonBorder,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = "Cancelar",
                    style = typography.labelMedium.copy(color = colors.onBackground)
                )
            }

        }
    }
}