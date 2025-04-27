package com.example.superid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.HorizontalPagerIndicator
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class IntroScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntroScreen()
        }
    }
}

@Composable
fun IntroScreen() {
    val context = LocalContext.current
    val yellow = Color(0xFFE2DA06)
    val darkGray = Color(0xFF131313)
    val textWhite = Color(0xFFFFFFFF)
    val textGray = Color(0xFFAFAFAF)

    val pagerState = rememberPagerState(initialPage = 0)
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = darkGray
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Ícone de voltar
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = textWhite,
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        context.startActivity(Intent(context, MainActivity::class.java))
                    }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pager de páginas
            HorizontalPager(
                pageCount = 3,
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Aqui você coloca a imagem da página
                    Image(
                        painter = painterResource(id = R.drawable.seu_icone_aqui),
                        contentDescription = "Imagem Intro",
                        modifier = Modifier
                            .size(200.dp)
                            .padding(16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Título
                    Text(
                        text = when (page) {
                            0 -> "Bem-vindo ao SuperId!"
                            1 -> "Segurança em primeiro lugar."
                            2 -> "Vamos começar!"
                            else -> ""
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = textWhite
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Descrição
                    Text(
                        text = when (page) {
                            0 -> "Armazene suas senhas com segurança e praticidade."
                            1 -> "Proteja seus dados com autenticação moderna."
                            2 -> "Crie sua conta e comece a usar!"
                            else -> ""
                        },
                        fontSize = 16.sp,
                        color = textGray,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Indicador de páginas
            HorizontalPagerIndicator(
                pagerState = pagerState,
                activeColor = yellow,
                inactiveColor = textGray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botão Continuar
            Button(
                onClick = {
                    if (pagerState.currentPage < 2) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        context.startActivity(Intent(context, LoginActivity::class.java))
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = yellow),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (pagerState.currentPage < 2) "Continuar" else "Começar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkGray
                )
            }
        }
    }
}