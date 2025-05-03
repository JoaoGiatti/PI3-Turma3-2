package com.example.superid

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.superid.ui.theme.SuperIDTheme
import kotlinx.coroutines.launch

class IntroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperIDTheme {
                IntroScreen()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntroScreen() {
    val context = LocalContext.current
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.background
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
                        .clickable { (context as? ComponentActivity)?.finish() }
                )

                Spacer(modifier = Modifier.weight(1f))

                Image(
                    painter = painterResource(id = R.drawable.superidlogowhiteyellow),
                    contentDescription = "Logo SuperID",
                    modifier = Modifier.height(24.dp)
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
                        style = typography.titleLarge,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Left,
                        color = colors.onBackground
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = when (page) {
                            0 -> "Armazene todas as suas senhas com segurança em um só lugar, protegidas por criptografia avançada."
                            1 -> "Realize o login em sites e aplicativos sem precisar lembrar daquela senha difícil! Escaneie um QRCode, e voilà!"
                            2 -> "Organize suas senhas como quiser! Categorize por sites, apps ou qualquer outra forma que te faça sentido."
                            3 -> "Agora que você já conhece o SuperID, vamos começar com a configuração da sua conta!"
                            else -> ""
                        },
                        style = typography.bodyLarge,
                        color = colors.onSurfaceVariant,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .wrapContentHeight()
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
                    val dotColor = if (pagerState.currentPage == index)
                        colors.primary else colors.onSurfaceVariant.copy(alpha = 0.5f)
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(10.dp)
                            .background(dotColor, shape = RoundedCornerShape(50))
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
                        val intent = Intent(context, TermsActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                        (context as? ComponentActivity)?.finish()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = if (pagerState.currentPage < 3) "Continuar" else "Começar",
                    style = typography.labelMedium,
                    color = colors.onPrimary
                )
            }
        }
    }
}
