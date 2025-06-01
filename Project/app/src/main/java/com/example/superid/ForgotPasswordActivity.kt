package com.example.superid

// Importações necessárias para componentes Android e Compose
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

// Activity principal da tela de recuperação de senha
class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recebe o parâmetro que indica se a navegação veio da tela de login
        val fromLogin = intent.getBooleanExtra("fromLogin", true)

        // Define o conteúdo da tela usando o tema do app
        setContent {
            SuperIDTheme {
                ForgotPasswordScreen(fromLogin = fromLogin)
            }
        }
    }
}

// Composable da tela de redefinição de senha
@Composable
fun ForgotPasswordScreen(fromLogin: Boolean) {
    val context = LocalContext.current               // Contexto para mostrar Toasts e finalizar Activity
    val auth: FirebaseAuth = Firebase.auth           // Instância do FirebaseAuth
    var email by remember { mutableStateOf("") }     // Estado do campo de email
    var emailError by remember { mutableStateOf(false) }  // Estado para controle de erro no email
    var isLoading by remember { mutableStateOf(false) }   // Estado de carregamento do botão
    val scrollState = rememberScrollState()          // Estado para scroll da tela
    val colors = MaterialTheme.colorScheme           // Paleta de cores do tema
    val typography = MaterialTheme.typography        // Tipografia do tema

    // Superfície principal da tela
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        // Coluna principal com padding e scroll
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo do app
            Image(
                painter = painterResource(id = R.drawable.superidlogowhiteyellow),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Título
            Text(
                text = "Redefinir Senha",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtítulo explicando o processo
            Text(
                text = "Digite seu email para receber o link de redefinição",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de texto para email com ícone de email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = "Email")
                },
                label = { Text("Email") },
                isError = emailError, // Exibe o estado de erro se for true
                supportingText = {
                    if (emailError) Text("Email inválido", color = MaterialTheme.colorScheme.error)
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botão de envio do email de redefinição
            Button(
                onClick = {
                    // Validação simples do email
                    if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = true
                        return@Button
                    }

                    // Desativa o botão e mostra o carregamento
                    isLoading = true

                    // Chamada do Firebase para enviar o link de redefinição
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                // Mostra mensagem de sucesso
                                Toast.makeText(
                                    context,
                                    "Email enviado! Verifique sua caixa de entrada.",
                                    Toast.LENGTH_LONG
                                ).show()
                                // Fecha a tela após envio
                                (context as? ComponentActivity)?.finish()
                            } else {
                                // Mostra erro em caso de falha
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
                shape = RoundedCornerShape(50.dp), // Bordas arredondadas
                enabled = !isLoading // Desativa o botão enquanto carrega
            ) {
                // Mostra carregamento ou texto no botão
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("Enviar Link de Redefinição")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão de cancelar que fecha a tela
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
