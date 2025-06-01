package com.example.superid

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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

// Activity principal que exibe a introdução do app
class IntroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Define o conteúdo da Activity usando Compose e aplica o tema personalizado
        setContent {
            SuperIDTheme {
                IntroScreen()  // Composable da tela de introdução
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)  // Habilita APIs experimentais do Foundation (como HorizontalPager)
@Composable
fun IntroScreen() {
    val context = LocalContext.current  // Contexto atual para iniciar intents etc.
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 4 })  // Estado do pager com 4 páginas
    val scope = rememberCoroutineScope()  // CoroutineScope para animações
    val colors = MaterialTheme.colorScheme  // Paleta de cores do tema atual
    val typography = MaterialTheme.typography  // Tipografia do tema atual
    val isDarkTheme = isSystemInDarkTheme()  // Verifica se o sistema está em tema escuro

    Surface(
        modifier = Modifier.fillMaxSize(),  // Preenche toda a tela
        color = colors.background  // Cor de fundo do tema
    ) {
        // Coluna que organiza o layout verticalmente
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .padding(bottom = 48.dp),
            verticalArrangement = Arrangement.SpaceBetween  // Espaça os elementos entre si, ocupando toda altura
        ) {
            // Linha para o logo no topo da tela
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 52.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))  // Espaço flexível para centralizar o logo

                // Seleciona o logo conforme tema (claro ou escuro)
                val imageResLogo = if (isDarkTheme) {
                    R.drawable.superidlogowhiteyellow  // Logo para fundo escuro
                } else {
                    R.drawable.superidlogoblackyellow  // Logo para fundo claro
                }
                Image(
                    painter = painterResource(id = imageResLogo),
                    contentDescription = "Logo SuperID",
                    modifier = Modifier.height(24.dp)  // Altura fixa do logo
                )

                Spacer(modifier = Modifier.weight(1f))  // Espaço flexível para centralizar o logo
            }

            Spacer(modifier = Modifier.height(16.dp))  // Espaço vertical

            // Pager horizontal para as páginas da introdução (swipe)
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)  // Ocupa o máximo possível da altura disponível
            ) { page ->  // Para cada página
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Imagem da página de introdução, escolhida conforme o índice
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
                            .fillMaxWidth(0.9f)  // Largura 90% da tela
                            .aspectRatio(1f)     // Mantém proporção 1:1 (quadrada)
                    )

                    Spacer(modifier = Modifier.height(0.dp))  // Espaço vazio (pode ser removido)

                    // Título do slide conforme página
                    Text(
                        text = when (page) {
                            0 -> "Segurança em 1° Lugar!"
                            1 -> "Login com QRCode!"
                            2 -> "Fácil e personalizável!"
                            3 -> "Vamos Começar!"
                            else -> ""
                        },
                        style = typography.titleLarge,  // Estilo de título grande do tema
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Left,  // Alinhamento à esquerda
                        color = colors.onBackground
                    )

                    Spacer(modifier = Modifier.height(12.dp))  // Espaço entre título e texto

                    // Texto descritivo do slide conforme página
                    Text(
                        text = when (page) {
                            0 -> "Armazene todas as suas senhas com segurança em um só lugar, protegidas por criptografia avançada."
                            1 -> "Realize o login em sites e aplicativos sem precisar lembrar daquela senha difícil! Escaneie um QRCode, e voilà!"
                            2 -> "Organize suas senhas como quiser! Categorize por sites, apps ou qualquer outra forma que te faça sentido."
                            3 -> "Agora que você já conhece o SuperID, vamos começar com a configuração da sua conta!"
                            else -> ""
                        },
                        style = typography.bodyLarge,  // Estilo corpo de texto grande do tema
                        color = colors.onSurfaceVariant,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .wrapContentHeight()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))  // Espaço abaixo do pager

            // Linha com os "dots" indicadores da página atual
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Cria 4 dots, um para cada página
                repeat(4) { index ->
                    val dotColor = if (pagerState.currentPage == index)
                        colors.primary  // Cor principal se for página atual
                    else
                        colors.onSurfaceVariant.copy(alpha = 0.5f)  // Cor cinza com transparência para as outras
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(10.dp)
                            .background(dotColor, shape = RoundedCornerShape(50))  // Dot circular
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))  // Espaço antes do botão

            // Botão para avançar na introdução ou começar o app
            Button(
                onClick = {
                    if (pagerState.currentPage < 3) {
                        // Se não estiver na última página, avança para a próxima com animação
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        // Se estiver na última página, inicia a TermsActivity e finaliza esta
                        val intent = Intent(context, TermsActivity::class.java)
                        // Flags para limpar a pilha e evitar voltar para essa Activity
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                        (context as? ComponentActivity)?.finish()  // Finaliza IntroActivity
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                shape = RoundedCornerShape(50.dp)  // Botão com bordas arredondadas
            ) {
                // Texto do botão muda entre "Continuar" e "Começar" dependendo da página atual
                Text(
                    text = if (pagerState.currentPage < 3) "Continuar" else "Começar",
                    style = typography.labelMedium,
                    color = colors.onPrimary
                )
            }
        }
    }
}
