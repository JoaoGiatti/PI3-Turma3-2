package com.example.superid

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.google.firebase.auth.FirebaseAuth



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
    val auth = FirebaseAuth.getInstance()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val yellow = Color(0xFFE2DA06)
    val darkGray = Color(0xFF131313)
    val textWhite = Color(0xFFFFFFFF)
    val textGray = Color(0xFFAFAFAF)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = darkGray,

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(50.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // Ícone de voltar
            Spacer(modifier = Modifier.padding(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Image(
                    painter = painterResource(id = R.drawable.arrowback),
                    contentDescription = "Voltar",
                    modifier = Modifier
                        .size(36.dp)
                        .clickable {
                            (context as? ComponentActivity)?.finish()
                        }
                )
                Spacer(modifier = Modifier.width(72.dp))
                Image(
                    painter = painterResource(id = R.drawable.superidlogowhiteyellow),
                    contentDescription = "Logo SuperID",
                    modifier = Modifier
                        .height(24.dp) // ajusta o tamanho se quiser
                )
            }

            Spacer(modifier = Modifier.height(72.dp))


            Text(
                text = "Vamos fazer seu login",
                color = textWhite,
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.interregular))
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Sentimos sua falta!",
                color = textWhite,
                fontSize = 24.sp,
                fontFamily = FontFamily(Font(R.font.poppinsbold))
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Campo de email
            Text(text = "Seu Email Mestre:", color = textWhite, fontFamily = FontFamily(Font(R.font.interbold)))
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("email.abc@gmail.com", color = textGray, fontFamily = FontFamily(Font(R.font.interbold))) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = yellow,
                    focusedBorderColor = yellow,
                    unfocusedTextColor = textWhite,
                    focusedTextColor = textWhite,
                    cursorColor = yellow
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Campo de senha
            Text(text = "Sua Senha Mestre:", color = textWhite, fontFamily = FontFamily(Font(R.font.interbold)))
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("***********", color = textGray, fontFamily = FontFamily(Font(R.font.interbold))) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(icon, contentDescription = null, tint = yellow)
                    }
                },
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
                fontFamily = FontFamily(Font(R.font.interregular)),
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(34.dp))

            // Botão de Entrar
            Button(
                onClick = {
                    // Lógica de autenticação
                    if (email.isNotBlank() && password.isNotBlank()) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    context.startActivity(Intent(context, HomeActivity::class.java))
                                } else {
                                    Toast.makeText(context, "Falha ao fazer login", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else{
                        Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = yellow),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("Entrar", color = Color.Black, fontFamily = FontFamily(Font(R.font.interbold)))
            }

            Spacer(modifier = Modifier.height(64.dp))

            // Cadastro
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Não tem uma conta? ", color = textWhite, fontFamily = FontFamily(Font(R.font.interbold)))
                Text(
                    "Cadastre-se",
                    color = textGray,
                    fontFamily = FontFamily(Font(R.font.interbold)),
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
                fontFamily = FontFamily(Font(R.font.intermedium)),
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}
