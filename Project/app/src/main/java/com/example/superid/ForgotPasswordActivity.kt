package com.example.superid

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.superid.ui.theme.SuperIDTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    var showReLoginAlert by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    // Estados para verificação de e-mail
    var isEmailVerified by remember { mutableStateOf(true) } // Inicialmente true para não mostrar o banner até verificar
    var showEmailNotVerifiedBanner by remember { mutableStateOf(false) }

    // Verificar o status de verificação do e-mail
    LaunchedEffect(Unit) {
        val user = auth.currentUser
        if (user != null) {
            user.reload().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isEmailVerified = user.isEmailVerified
                    // Mostrar banner apenas se o email não estiver verificado
                    showEmailNotVerifiedBanner = !user.isEmailVerified
                } else {
                    Toast.makeText(context, "Erro ao verificar e-mail.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Função para reenviar email de verificação
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
                        error.contains("too many", true) -> "Muitas tentativas. Tente mais tarde"
                        else -> "Falha ao enviar: $error"
                    }
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }
    }

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
            // Banner de e-mail não verificado
            if (showEmailNotVerifiedBanner) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.error, RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.alert),
                            contentDescription = "Alerta",
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Text(
                                text = "Valide seu e-mail para redefinir a senha",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Clique aqui para reenviar o email de verificação",
                                color = Color.White.copy(alpha = 0.9f),
                                style = typography.labelSmall,
                                modifier = Modifier.clickable { resendVerificationEmail() }
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            painter = painterResource(id = R.drawable.x), // Você precisa ter um ícone de fechar
                            contentDescription = "Fechar",
                            tint = Color.White,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { showEmailNotVerifiedBanner = false }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

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

            // Botão de Redefinir Senha
            Button(
                onClick = {
                    // Validação do email
                    emailError = email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

                    if (!emailError) {
                        // Verificar se o email está verificado
                        if (!isEmailVerified) {
                            Toast.makeText(
                                context,
                                "Valide seu e-mail antes de redefinir a senha",
                                Toast.LENGTH_LONG
                            ).show()
                            return@Button
                        }

                        isLoading = true
                        auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    showReLoginAlert = true // Mostra o alerta
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Erro: ${task.exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFF00)),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.Black)
                } else {
                    Text(
                        "Enviar Link de Redefinição",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Voltar
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

    // Alerta personalizado para login necessário
    if (showReLoginAlert) {
        Dialog(
            onDismissRequest = { showReLoginAlert = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .background(Color(0xFF1C1C1E), shape = RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.alert), // Ícone de informação
                        contentDescription = "Informação",
                        tint = Color(0xFFFFFF00),
                        modifier = Modifier
                            .size(40.dp)
                            .padding(bottom = 16.dp)
                    )

                    Text(
                        "Atenção!",
                        color = Color(0xFFFFFF00),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        " Depois de redefinir sua senha faça login novamente para atualizar seus dados .",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Botões lado a lado
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Botão para fazer login depois
                        OutlinedButton(
                            onClick = { showReLoginAlert = false },
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            ),
                            border = BorderStroke(
                                1.dp,
                                Color(0xFFFFFF00)
                            ),
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        ) {
                            Text("Depois", fontWeight = FontWeight.Bold)
                        }

                        // Botão para fazer login agora
                        Button(
                            onClick = {
                                showReLoginAlert = false
                                context.startActivity(Intent(context, LogInActivity::class.java))
                                (context as? ComponentActivity)?.finishAffinity()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFF00)),
                            modifier = Modifier.weight(1f).padding(start = 8.dp)
                        ) {
                            Text("Login", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}