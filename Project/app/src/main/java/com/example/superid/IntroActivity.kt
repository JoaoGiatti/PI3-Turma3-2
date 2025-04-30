package com.example.superid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class IntroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IntroScreen()
        }
    }
}

@Preview
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntroScreen() {
    val context = LocalContext.current
    val yellow = Color(0xFFE2DA06)
    val darkGray = Color(0xFF131313)
    val darkGrayLigher = Color(0xFF161616)
    val textWhite = Color(0xFFFFFFFF)
    val textGray = Color(0xFFAFAFAF)

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 4 }
    )
    val scope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = darkGrayLigher
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .padding(bottom = 48.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 52.dp)
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

                Spacer(modifier = Modifier.weight(1f))

                Image(
                    painter = painterResource(id = R.drawable.superidlogowhiteyellow),
                    contentDescription = "Logo SuperID",
                    modifier = Modifier
                        .height(24.dp)
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = when (page) {
                            0 -> painterResource(id = R.drawable.intro1)
                            1 -> painterResource(id = R.drawable.intro2)
                            2 -> painterResource(id = R.drawable.intro3)
                            3 -> painterResource(id = R.drawable.intro4)
                            else -> painterResource(id = R.drawable.intro1)
                        },
                        contentDescription = "Imagem Intro",
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .aspectRatio(1f)
                    )

                    Spacer(modifier = Modifier.height(0.dp))

                    Text(
                        text = when (page) {
                            0 -> "Segurança em 1° Lugar!"
                            1 -> "Login com QRCode!"
                            2 -> "Fácil e personalizável!"
                            3 -> "Vamos Começar!"
                            else -> ""
                        },
                        fontSize = 24.sp,
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                            .fillMaxWidth(),
                        fontFamily = FontFamily(Font(R.font.poppinsbold)),
                        textAlign = TextAlign.Left,
                        color = textWhite
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = when (page) {
                            0 -> "Armazene todas as suas senhas com segurança em um só lugar, protegidas por criptografia avançada."
                            1 -> "Realize o login em sites e aplicativos sem precisar lembrar daquela senha difícil! Escaneie um QRCode, e voilà!"
                            2 -> "Organize suas senhas como quiser! Categoriz por sites, apps ou qualquer outra forma que te faça sentido"
                            3 -> "Agora que você já conhece o SuperID, vamos começçar com a configuração da sua conta!"
                            else -> ""
                        },
                        fontSize = 18.sp,
                        color = textGray,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .wrapContentHeight(),
                        fontFamily = FontFamily(Font(R.font.interregular))
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(4) { index ->
                    val color = if (pagerState.currentPage == index) yellow else textGray
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(10.dp)
                            .background(color, shape = RoundedCornerShape(50))
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (pagerState.currentPage < 3) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        sharedPref.edit().putBoolean("tutorial_visto", true).apply()

                        val termosAceitos = sharedPref.getBoolean("termos_aceitos", false)
                        val nextActivity = if (termosAceitos) {
                            MainActivity::class.java
                        } else {
                            TermsActivity::class.java
                        }

                        val intent = Intent(context, nextActivity)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)

                        (context as? ComponentActivity)?.finish()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = yellow),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = if (pagerState.currentPage < 3) "Continuar" else "Começar",
                    fontSize = 18.sp,
                    fontFamily = FontFamily(Font(R.font.interbold)),
                    color = darkGray
                )
            }
        }
    }
}
