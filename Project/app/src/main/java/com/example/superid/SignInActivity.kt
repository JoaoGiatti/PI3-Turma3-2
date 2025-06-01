package com.example.superid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
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
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.sp
import com.example.superid.ui.theme.SuperIDTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Gerenciador de criptografia seguro usando AES-256-GCM com Android Keystore
 */
class SecureCryptoManager(context: Context) {

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "SuperID_AES256_Key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_SIZE = 12 // GCM recomenda 12 bytes
        private const val TAG_LENGTH = 128 // bits para autenticação GCM
    }

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    init {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            createKey()
        }
    }

    private fun createKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        val keySpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setUserAuthenticationRequired(false)
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(keySpec)
        keyGenerator.generateKey()
    }

    private fun getKey(): SecretKey {
        return (keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
    }

    fun encrypt(data: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val key = getKey()

        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(data.toByteArray(Charsets.UTF_8))

        // Combine IV + encrypted data
        val combined = iv + encryptedBytes
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }
}

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

@Composable
fun PasswordRequirementItem(
    isValid: Boolean,
    text: String,
    colors: ColorScheme
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        val iconColor = if (isValid) colors.primary else colors.outline

        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Requisito de senha",
            tint = iconColor,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = if (isValid) colors.primary else colors.outline
        )
    }
}

@Composable
fun SignInScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val isDarkTheme = isSystemInDarkTheme()

    // Estados do formulário
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

    // Inicializa o gerenciador de criptografia
    val cryptoManager = remember { SecureCryptoManager(context) }

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

            // Cabeçalho com botão de voltar e logo
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                val imageResArrow = if (isDarkTheme) R.drawable.arrowback else R.drawable.arrowbackblack
                Image(
                    painter = painterResource(id = imageResArrow),
                    contentDescription = "Voltar",
                    modifier = Modifier
                        .size(38.dp)
                        .clickable { (context as? ComponentActivity)?.finish() }
                )
                Spacer(modifier = Modifier.width(72.dp))
                val imageResLogo = if (isDarkTheme) R.drawable.superidlogowhiteyellow else R.drawable.superidlogoblackyellow
                Image(
                    painter = painterResource(id = imageResLogo),
                    contentDescription = "Logo SuperID",
                    modifier = Modifier.height(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(72.dp))

            // Títulos da tela
            Text("Você é novo?", style = typography.labelMedium, color = colors.onBackground)
            Text("Vamos te cadastrar!", style = typography.titleLarge, color = colors.onBackground)

            Spacer(modifier = Modifier.height(24.dp))

            // Campo de nome completo
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
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
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

            // Campo de e-mail
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
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
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

            // Campo de senha
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
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar senha" else "Mostrar senha",
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
            Spacer(modifier=Modifier.height(4.dp))
            Column(modifier = Modifier.padding(top = 4.dp)) {
                PasswordRequirementItem(
                    isValid = password.length >= 6,
                    text = "Mínimo 6 caracteres",
                    colors = colors
                )
                PasswordRequirementItem(
                    isValid = password.any { it.isDigit() },
                    text = "Pelo menos 1 número",
                    colors = colors
                )
                PasswordRequirementItem(
                    isValid = password.any { !it.isLetterOrDigit() },
                    text = "Pelo menos 1 caractere especial",
                    colors = colors
                )
                PasswordRequirementItem(
                    isValid = password.any { it.isUpperCase() },
                    text = "Pelo menos 1 letra maiúscula",
                    colors = colors
                )
            }

            if (passwordError.isNotEmpty()) {
                Text(passwordError, color = colors.error, style = typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botão de cadastro
            Button(
                onClick = {
                    var isValid = true
                    errorMessage = ""

                    // Validação dos campos
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
                    } else if (!password.any { it.isDigit() }) {
                        passwordError = "Senha deve conter pelo menos 1 número"
                        isValid = false
                    } else if (!password.any { !it.isLetterOrDigit() }) {
                        passwordError = "Senha deve conter pelo menos 1 caractere especial"
                        isValid = false
                    } else if (!password.any { it.isUpperCase() }) {
                        passwordError = "Senha deve conter pelo menos 1 letra maiúscula"
                        isValid = false
                    }

                    if (isValid) {
                        isLoading = true
                        val encryptedPassword = cryptoManager.encrypt(password)

                        // Criação de usuário no Firebase Auth
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                                    val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

                                    // Dados do usuário para salvar no Firestore
                                    val userData = hashMapOf(
                                        "UID" to uid,
                                        "IMEI" to androidId,
                                        "emailMestre" to email,
                                        "nome" to name,
                                        "senhaMestre" to encryptedPassword
                                    )

                                    // Salva os dados no Firestore
                                    firestore.collection("users_data")
                                        .document(uid)
                                        .set(userData)
                                        .addOnSuccessListener {
                                            // Envia e-mail de verificação
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