package com.example.superid

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.superid.ui.theme.SuperIDTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperIDTheme {
                SignInScreen()
            }
        }
    }
}

fun encryptPassword(password: String): String {
    val secretKey = "1234567890123456"
    val key: Key = SecretKeySpec(secretKey.toByteArray(), "AES")
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.ENCRYPT_MODE, key)
    val encryptedBytes = cipher.doFinal(password.toByteArray())
    return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
}

@Composable
fun SignInScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val isDarkTheme = isSystemInDarkTheme()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

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

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                val imageResArrow = if (isDarkTheme) {
                    R.drawable.arrowback  // logo para fundo escuro
                } else {
                    R.drawable.arrowbackblack  // logo para fundo claro
                }
                Image(
                    painter = painterResource(id = imageResArrow),
                    contentDescription = "Voltar",
                    modifier = Modifier
                        .size(38.dp)
                        .clickable { (context as? ComponentActivity)?.finish() }
                )
                Spacer(modifier = Modifier.width(72.dp))
                val imageResLogo = if (isDarkTheme) {
                    R.drawable.superidlogowhiteyellow  // logo para fundo escuro
                } else {
                    R.drawable.superidlogoblackyellow  // logo para fundo claro
                }
                Image(
                    painter = painterResource(id = imageResLogo),
                    contentDescription = "Logo SuperID",
                    modifier = Modifier.height(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(72.dp))

            Text("Você é novo?", style = typography.labelMedium, color = colors.onBackground)
            Text("Vamos te cadastrar!", style = typography.titleLarge, color = colors.onBackground)

            Spacer(modifier = Modifier.height(24.dp))

            Text("Nome completo:", style = typography.labelMedium, color = colors.onBackground)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = ""
                },
                placeholder = { Text("Seu nome completo", color = colors.outline, style = typography.labelMedium) },
                isError = nameError.isNotEmpty(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.primary,
                    focusedTextColor = colors.onBackground,
                    unfocusedTextColor = colors.onBackground,
                    cursorColor = colors.primary
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (nameError.isNotEmpty()) {
                Text(nameError, color = colors.error, style = typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text("Seu melhor e-mail:", style = typography.labelMedium, color = colors.onBackground)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = ""
                },
                placeholder = { Text("exemplo@email.com", color = colors.outline, style = typography.labelMedium) },
                isError = emailError.isNotEmpty(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.primary,
                    focusedTextColor = colors.onBackground,
                    unfocusedTextColor = colors.onBackground,
                    cursorColor = colors.primary
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (emailError.isNotEmpty()) {
                Text(emailError, color = colors.error, style = typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Crie uma senha segura:", style = typography.labelMedium, color = colors.onBackground)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = ""
                },
                placeholder = { Text("***********", color = colors.outline, style = typography.labelMedium) },
                isError = passwordError.isNotEmpty(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = colors.primary
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.primary,
                    focusedTextColor = colors.onBackground,
                    unfocusedTextColor = colors.onBackground,
                    cursorColor = colors.primary
                ),
                modifier = Modifier.fillMaxWidth()
            )
            if (passwordError.isNotEmpty()) {
                Text(passwordError, color = colors.error, style = typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(24.dp))

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

                    if (isValid) {
                        isLoading = true
                        val encryptedPassword = encryptPassword(password)

                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                                    val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

                                    val userData = hashMapOf(
                                        "UID" to uid,
                                        "IMEI" to androidId,
                                        "emailMestre" to email,
                                        "nome" to name,
                                        "senhaMestre" to encryptedPassword
                                    )

                                    firestore.collection("Users")
                                        .document(uid)
                                        .set(userData)
                                        .addOnSuccessListener {
                                            auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                                                if (verifyTask.isSuccessful) {
                                                    context.startActivity(Intent(context, EmailVerificationActivity::class.java))
                                                    (context as? ComponentActivity)?.finish()
                                                } else {
                                                    errorMessage = verifyTask.exception?.message ?: "Erro ao enviar email de verificação"
                                                }
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            errorMessage = "Erro ao salvar dados: ${e.localizedMessage}"
                                        }
                                } else {
                                    errorMessage = task.exception?.message ?: "Erro desconhecido"
                                }
                            }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = colors.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("Cadastrar", color = colors.onPrimary, style = typography.labelMedium)
                }
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = colors.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
