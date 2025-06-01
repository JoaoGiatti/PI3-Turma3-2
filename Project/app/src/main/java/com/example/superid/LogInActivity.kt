// Declaração do pacote
package com.example.superid

// Importações necessárias
import android.content.Context
import android.content.Intent
import android.os.Bundle

import com.example.superid.SecureCryptoManager

// Remova a declaração completa da classe SecureCryptoManager que está duplicada
// Mantenha apenas o código do LoginScreen usando a classe importada
import android.provider.Settings
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.ui.theme.SuperIDTheme
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Gerenciador de criptografia seguro usando AES-256-GCM com Android Keystore
 * (Exatamente igual ao usado na página de cadastro)
 */


/**
 * Activity responsável pela tela de login
 */
class LogInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Define o conteúdo da tela usando Compose
        setContent {
            SuperIDTheme {
                LogInScreen()
            }
        }
    }
}

/**
 * Tela de login composable
 */
@Composable
fun LogInScreen() {
    // Contexto atual para acesso a recursos Android
    val context = LocalContext.current
    // Instância do Firebase Authentication
    val auth = FirebaseAuth.getInstance()
    // Estado para rolagem da tela
    val scrollState = rememberScrollState()
    // Instância do Firestore para operações com banco de dados
    val db = FirebaseFirestore.getInstance()
    // Instância do SecureCryptoManager (igual ao cadastro)
    val cryptoManager = remember { SecureCryptoManager(context) }

    // Estados para os campos de email e senha
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    // Estado para visibilidade da senha
    var passwordVisible by remember { mutableStateOf(false) }

    // Estados para erros de validação
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    // Cores e tipografia do tema
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    // Verifica se o tema escuro está ativo
    val isDarkTheme = isSystemInDarkTheme()

    // Layout principal da tela
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(50.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Linha com botão de voltar e logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Seleciona a imagem da seta de voltar baseado no tema
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
                        .clickable { (context as? ComponentActivity)?.finish() }
                )
                Spacer(modifier = Modifier.width(72.dp))

                // Seleciona o logo baseado no tema
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

            Spacer(modifier = Modifier.height(72.dp))

            // Títulos de boas-vindas
            Text(
                text = "Vamos fazer seu login",
                color = colors.onBackground,
                style = typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Sentimos sua falta!",
                color = colors.onBackground,
                style = typography.titleLarge
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Campo de email
            Text(
                text = "Seu Email Mestre:",
                color = colors.onBackground,
                style = typography.labelMedium
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (emailError) emailError = false
                },
                isError = emailError,
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                supportingText = {
                    if (emailError) Text("O campo de e-mail é obrigatório", color = MaterialTheme.colorScheme.error)
                },
                placeholder = {
                    Text(
                        "email.abc@gmail.com",
                        color = colors.outline,
                        style = typography.labelMedium
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = colors.primary,
                    focusedBorderColor = colors.primary,
                    unfocusedTextColor = colors.onBackground,
                    focusedTextColor = colors.onBackground,
                    cursorColor = colors.primary,

                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorCursorColor = MaterialTheme.colorScheme.error,
                    errorTextColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Campo de senha
            Text(
                text = "Sua Senha Mestre:",
                color = colors.onBackground,
                style = typography.labelMedium
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (passwordError) passwordError = false
                },
                isError = passwordError,
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                supportingText = {
                    if (passwordError) Text("O campo de senha é obrigatório", color = MaterialTheme.colorScheme.error)
                },
                placeholder = {
                    Text(
                        "***********",
                        color = colors.outline,
                        style = typography.labelMedium
                    )
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = colors.primary
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = colors.primary,
                    focusedBorderColor = colors.primary,
                    unfocusedTextColor = colors.onBackground,
                    focusedTextColor = colors.onBackground,
                    cursorColor = colors.primary,
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorCursorColor = MaterialTheme.colorScheme.error,
                    errorTextColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botão "Esqueceu a senha?"
            TextButton(
                onClick = {
                    val intent = Intent(context, ForgotPasswordActivity::class.java).apply {
                        putExtra("fromLogin", true) // Indica que veio do login
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 4.dp)
            ) {
                Text(
                    "Esqueceu a senha?",
                    color = colors.secondary,
                    style = typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(34.dp))

            // Botão de login
            Button(
                onClick = {
                    // Validação dos campos
                    var valid = true
                    if (email.isBlank()) {
                        emailError = true
                        valid = false
                    }
                    if (password.isBlank()) {
                        passwordError = true
                        valid = false
                    }

                    if (valid) {
                        // Tentativa de login com Firebase Auth
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    // Criptografa a senha (igual ao cadastro)
                                    val encryptedPassword = cryptoManager.encrypt(password)

                                    // Atualiza a senha no Firestore
                                    db.collection("users_data")
                                        .document(user?.uid ?: "")
                                        .update("senhaMestre", encryptedPassword)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Bem-vindo!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            context.startActivity(Intent(context, HomeActivity::class.java))
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(
                                                context,
                                                "Login feito, mas erro ao salvar senha: ${e.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                } else {
                                    // Tratamento de erros específicos
                                    val message = when (val e = task.exception) {
                                        is FirebaseAuthInvalidCredentialsException,
                                        is FirebaseAuthInvalidUserException -> "Email ou senha não encontrado."
                                        is FirebaseNetworkException -> "Sem conexão com a internet."
                                        else -> "Erro ao fazer login. Tente novamente."
                                    }
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Entrar",
                    color = colors.onPrimary,
                    style = typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(64.dp))

            // Link para cadastro
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Não tem uma conta? ",
                    color = colors.onBackground,
                    style = typography.labelMedium
                )
                Text(
                    text = "Cadastre-se",
                    color = colors.secondary,
                    style = typography.labelMedium,
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(context, SignInActivity::class.java))
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            Divider(color = colors.primary, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Termos de serviço e política de privacidade
            Text(
                text = "Ao continuar você concorda com os Termos de Serviço e Política de Privacidade ",
                color = colors.outline,
                style = typography.labelSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}