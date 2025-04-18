package com.example.superid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.Visibility
//import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource


class LogInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LogInScreen()
        }
    }
}

@Composable
fun LogInScreen() {
    val context = LocalContext.current

    val yellow = Color(0xFFE2DA06)
    val darkGray = Color(0xFF131313)
    val textWhite = Color(0xFFFFFFFF)
    val textGray = Color(0xFFAFAFAF)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = darkGray
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // Ícone de voltar
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = textWhite,
                modifier = Modifier
                    .size(36.dp)
                    .clickable { context.startActivity(Intent(context, MainActivity::class.java)) }
            )

            Spacer(modifier = Modifier.height(1.dp))

            // Logo centralizada
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.superidlogowhiteyellow),
                    contentDescription = "Logo Super ID",
                    modifier = Modifier.size(120.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Vamos fazer seu login",
                color = textWhite,
                fontSize = 16.sp
            )

            Text(
                text = "Sentimos sua falta!",
                color = textWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo de email
            Text(text = "Seu Email:", color = textWhite)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("email.abc@gmail.com", color = textGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = yellow,
                    focusedBorderColor = yellow,
                    unfocusedTextColor = textWhite,
                    focusedTextColor = textWhite,
                    cursorColor = yellow
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de senha
            Text(text = "Sua Senha Mestre:", color = textWhite)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("***********", color = textGray) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                trailingIcon = {
//                    val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
//                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
//                        Icon(icon, contentDescription = null, tint = yellow)
//                    }
//                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = yellow,
                    focusedBorderColor = yellow,
                    unfocusedTextColor = textWhite,
                    focusedTextColor = textWhite,
                    cursorColor = yellow
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Esqueceu a senha
            Text(
                text = "Esqueceu a senha?",
                color = textWhite,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botão de Entrar
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = yellow),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("Entrar", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Cadastro
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Não tem uma conta? ", color = textWhite)
                Text(
                    "Cadastre-se",
                    color = textGray,
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(context, SignInActivity::class.java))
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Divider(color = yellow, thickness = 1.dp)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Ao continuar você concorda com os Termos de Serviço\n" +
                        "e Política de Privacidade da Nome",
                color = textGray,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}
