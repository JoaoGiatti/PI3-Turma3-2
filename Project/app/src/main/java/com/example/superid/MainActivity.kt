package com.example.superid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntOffset

// Cores personalizadas
val Yellow = Color(0xFFE2DA06)
val DarkGray = Color(0xFF131313)
val White = Color(0xFFFFFFFF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperIDScreen()
        }
    }
}

@Composable
fun SuperIDScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo SuperID mais pra cima com offset negativo
        Image(
            painter = painterResource(id = R.drawable.superidlogowhiteyellow),
            contentDescription = "Logo SuperID",
            modifier = Modifier
                .height(24.dp)
                .offset { IntOffset(x = 0, y = -180) } // Sobe o logo 180 pixels
        )

        Spacer(modifier = Modifier.height(100.dp)) // Espaço antes do ícone

        Image(
            painter = painterResource(id = R.drawable.walletvector),
            contentDescription = "Wallet Icon",
            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = buildAnnotatedString {
                append("Um modo inovador de fazer login ")
                pushStyle(SpanStyle(color = Yellow))
                append("sem usar senhas.")
                pop()
            },
            color = White,
            fontSize = 24.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(horizontal = 12.dp)
        )


        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { /* Ação de login */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Yellow,
                contentColor = DarkGray
            ),
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(text = "Entrar na conta")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { /* Ação de registro */ },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = White
            ),
            modifier = Modifier.fillMaxWidth(0.8f),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(text = "Registrar")
        }
    }
}