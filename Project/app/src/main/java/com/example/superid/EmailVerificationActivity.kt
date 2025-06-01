// Importação de pacotes necessários do Android, Compose e Firebase
package com.example.superid

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.example.superid.ui.theme.SuperIDTheme

// Activity responsável por exibir a tela de verificação de e-mail
class EmailVerificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Define o conteúdo da tela utilizando Compose e o tema da aplicação
        setContent {
            SuperIDTheme {
                EmailVerificationScreen()
            }
        }
    }
}

@Composable
fun EmailVerificationScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var emailVerified by remember { mutableStateOf(user?.isEmailVerified ?: false) }
    var showAlertDialog by remember { mutableStateOf(false) }
    val isDarkTheme = isSystemInDarkTheme()

    // Função chamada ao clicar em "Prosseguir" para checar se o e-mail foi verificado
    fun verifyEmail() {
        user?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                emailVerified = user?.isEmailVerified == true
                if (emailVerified) {
                    // Vai para a tela Home se o e-mail estiver verificado
                    context.startActivity(Intent(context, HomeActivity::class.java))
                    (context as? ComponentActivity)?.finish()
                } else {
                    // Mostra mensagem caso o e-mail ainda não tenha sido verificado
                    Toast.makeText(
                        context,
                        "E-mail não confirmado. Verifique sua caixa de entrada!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                // Mostra erro em caso de falha na verificação
                Toast.makeText(context, "Erro ao verificar o e-mail.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Mostra um alerta se o usuário optar por prosseguir sem verificar o e-mail
    fun proceedWithoutVerification() {
        showAlertDialog = true
    }

    // Exibe o AlertDialog caso o usuário tente prosseguir sem verificação
    if (showAlertDialog) {
        val colorRes = if (isDarkTheme) Color(0xFF323232) else Color(0xFFECECEC)
        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            containerColor = colorRes,
            title = {
                Text("Atenção!", color = MaterialTheme.colorScheme.primary)
            },
            text = {
                Text(
                    "Caso você não valide seu e-mail, não poderá usar a funcionalidade de Login Sem Senha. Deseja continuar?",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    // Vai para a Home mesmo sem e-mail verificado
                    context.startActivity(Intent(context, HomeActivity::class.java))
                    (context as? ComponentActivity)?.finish()
                }) {
                    Text("Sim", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAlertDialog = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSecondary)
                }
            }
        )
    }

    // Componente principal da tela
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.padding(22.dp))

            // Linha com botão de voltar e logo da aplicação
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.width(24.dp))

                // Ícone de voltar (diferente para tema claro/escuro)
                val imageResArrow = if (isDarkTheme) {
                    R.drawable.arrowback
                } else {
                    R.drawable.arrowbackblack
                }
                Image(
                    painter = painterResource(id = imageResArrow),
                    contentDescription = "Voltar",
                    modifier = Modifier
                        .size(36.dp)
                        .clickable {
                            (context as? ComponentActivity)?.finish()
                        }
                )

                Spacer(modifier = Modifier.width(72.dp))

                // Logo SuperID (claro ou escuro, conforme o tema)
                val imageResLogo = if (isDarkTheme) {
                    R.drawable.superidlogowhiteyellow
                } else {
                    R.drawable.superidlogoblackyellow
                }
                Image(
                    painter = painterResource(id = imageResLogo),
                    contentDescription = "Logo SuperID",
                    modifier = Modifier.height(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(44.dp))

            // Título principal
            Text(
                text = "Te enviamos um email\nde confirmação!",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(22.dp)
            )

            // Subtítulo explicativo
            Text(
                text = "Confirme a sua identidade antes de prosseguir",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(start = 38.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Ícone de email centralizado
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.emailicon),
                    contentDescription = "Email Icon",
                    modifier = Modifier.size(200.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Texto explicando o próximo passo
            Text(
                text = "Após confirmar o email, clique em\nProsseguir.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Botão "Prosseguir" (verifica se o email foi confirmado)
            Button(
                onClick = { verifyEmail() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text(
                    text = "Prosseguir",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            // Botão "Prosseguir sem verificar"
            Button(
                onClick = { proceedWithoutVerification() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text(
                    text = "Prosseguir sem verificar",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
