package com.example.superid.homepages

import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.superid.R
import com.example.superid.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import android.util.Base64
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.style.TextDecoration
import com.example.superid.ForgotPasswordActivity
import com.example.superid.MainActivity
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.security.Key


fun decryptPassword(encryptedPassword: String): String {
    val secretKey = "1234567890123456"
    val key: Key = SecretKeySpec(secretKey.toByteArray(), "AES")
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.DECRYPT_MODE, key)
    val decodedBytes = Base64.decode(encryptedPassword, Base64.DEFAULT)
    val decryptedBytes = cipher.doFinal(decodedBytes)
    return String(decryptedBytes)

}

@Composable
fun ProfilePage(viewModel: ProfileViewModel = viewModel()) {
    val item = viewModel.userItem.value
    val context = LocalContext.current
    val colors = MaterialTheme.colors


    // Estado para verificar se o e-mail está verificado
    var isEmailVerified by remember { mutableStateOf(true) }

    val decryptedPassword = remember(item.senhaMestre) {
        try {
            decryptPassword(item.senhaMestre)
        } catch (e: Exception) {
            "Erro ao descriptografar"
        }
    }

    val auth = FirebaseAuth.getInstance()
    var emailVerified by remember { mutableStateOf(false) }

    val typography = androidx.compose.material3.MaterialTheme.typography

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
                        error.contains(
                            "too many",
                            true
                        ) -> "Muitas tentativas seguidas. Aguarde cerca de 1 minuto antes de tentar novamente."

                        else -> "Falha ao enviar: Muitas tentativas seguidas. Aguarde cerca de 1 minuto antes de tentar novamente."
                    }
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }
            }
    }


        // Verificar o status de verificação do e-mail
    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                isEmailVerified = user.isEmailVerified
            } else {
                Toast.makeText(context, "Erro ao verificar o e-mail.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(vertical = 24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top

    ) {

        // Topo: logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            val imageResLogo = R.drawable.superid
            Image(
                painter = painterResource(id = imageResLogo),
                contentDescription = "Logo SuperID",
                modifier = Modifier.height(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Alerta de verificação de e-mail - AGORA É UM BOTÃO CLICÁVEL
        if (!isEmailVerified) {
            Spacer(modifier = Modifier.height(12.dp))

            // Botão para reenviar email de verificação
            Button(
                onClick = { resendVerificationEmail() },
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(40.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.alert),
                        contentDescription = "Alerta",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = "Clique para reenviar o email de verificação",
                        color = Color.White,
                        style = MaterialTheme.typography.subtitle2
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Avatar
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = R.drawable.profileicon),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
                    .border(3.dp, Color.White, CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nome
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = item.nome,
                color = Color.White,
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
            )

        }

        Spacer(modifier = Modifier.height(32.dp))

        // Divisor horizontal
        Divider(
            color = Color.DarkGray,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Campos de informação
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            // Campo Nome
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nome:",
                    color = Color.White,
                    style = typography.bodyLarge
                )
                Text(
                    text = item.nome,
                    color = Color.White,
                    style = typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.DarkGray, thickness = 1.dp)

            // Campo Email
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Email:",
                    color = Color.White,
                    style = typography.bodyLarge
                )
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(
                        text = item.emailMestre,
                        color = Color.Gray,
                        style = typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (!isEmailVerified) {
                        Text(
                            text = "Valide seu Email",
                            color = Color.Yellow,
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier
                                .clickable { resendVerificationEmail() }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.DarkGray, thickness = 1.dp)

            // Campo Senha
            Spacer(modifier = Modifier.height(16.dp))

            // Estado para controlar a visibilidade da senha
            var isPasswordVisible by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Senha:",
                    color = Color.White,
                    style = typography.bodyLarge
                )
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isPasswordVisible) decryptedPassword else "••••••••",
                            color = Color.Gray,
                            style = typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (isPasswordVisible) "Ocultar senha" else "Mostrar senha",
                            tint = Color.White,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { isPasswordVisible = !isPasswordVisible }
                        )
                    }

                    TextButton(
                        onClick = {
                            if (isEmailVerified) {
                                val intent = Intent(context, ForgotPasswordActivity::class.java).apply {
                                    putExtra("fromLogin", true)
                                }
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, "Você precisa validar seu e-mail antes de redefinir a senha.", Toast.LENGTH_LONG).show()
                            }
                        }
                    ) {
                        Text(
                            text = "Redefinir senha",
                            color = Color.Yellow,
                            style = MaterialTheme.typography.caption.copy(textDecoration = TextDecoration.Underline),
                        )
                    }

                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Divider(color = Color.DarkGray, thickness = 1.dp)

            // Espaço extra para o botão não cobrir conteúdo
            Spacer(modifier = Modifier.height(70.dp))

            // Botão Sair
            Button(
                onClick = {
                    context.startActivity(Intent(context, MainActivity::class.java))
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFE2DA06),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Sair", fontWeight = FontWeight.Bold)
            }
        }
    }
}