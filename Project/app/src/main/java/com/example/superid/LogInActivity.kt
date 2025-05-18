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

class LogInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperIDTheme {
                LogInScreen()
            }
        }
    }
}

@Composable
fun LogInScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val scrollState = rememberScrollState()
    val db = FirebaseFirestore.getInstance()


    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    val isDarkTheme = isSystemInDarkTheme()

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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
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

            Button(
                onClick = {
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
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    val encryptedPassword = encryptPassword(password)

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
                                    val message = when (val e = task.exception) {
                                        is FirebaseAuthInvalidCredentialsException,
                                        is FirebaseAuthInvalidUserException -> "Email ou senha inválidos."
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

            Text(
                text = "Ao continuar você concorda com os Termos de Serviço e Política de Privacidade ",
                color = colors.outline,
                style = typography.labelSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
