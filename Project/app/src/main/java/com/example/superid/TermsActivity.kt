package com.example.superid


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.ui.theme.SuperIDTheme

// Atividade que exibe os termos de uso
class TermsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Lê o conteúdo do arquivo de termos
        val termos = resources.openRawResource(R.raw.termos)
            .bufferedReader().use { it.readText() }

        // Define o conteúdo da tela usando o tema personalizado
        setContent {
            SuperIDTheme {
                TermsScreen(termos)
            }
        }
    }
}

@Composable
fun TermsScreen(termosText: String) {
    val scrollState = rememberScrollState() // Estado da rolagem do conteúdo dos termos
    var containerHeightPx by remember { mutableStateOf(0) } // Altura do contêiner usada para calcular o thumb da barra de rolagem
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme() // Verifica se o sistema está em tema escuro

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Exibe o logo conforme o tema (claro ou escuro)
        val imageResLogo = if (isDarkTheme) {
            R.drawable.superidlogowhiteyellow
        } else {
            R.drawable.superidlogoblackyellow
        }
        Image(
            painter = painterResource(id = imageResLogo),
            contentDescription = "Logo SuperID",
            modifier = Modifier.size(100.dp)
        )

        // Título principal da tela
        Text(
            text = "Termos de Uso e Privacidade",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Caixa contendo os termos com barra de rolagem e scroll customizado
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.70f)
        ) {
            val colorResBox = if (isDarkTheme) Color(0xFF3D3D3D) else Color(0xFFC4C4C4)

            Surface(
                modifier = Modifier.matchParentSize(),
                color = colorResBox,
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 4.dp
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Coluna do conteúdo com rolagem vertical
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .verticalScroll(scrollState)
                        ) {
                            // Quebra o texto dos termos em linhas e exibe conforme seu padrão
                            termosText.split("\n").forEach { line ->
                                when {
                                    line.trim().startsWith("━") -> {
                                        Divider(
                                            color = Color.Gray,
                                            thickness = 1.dp,
                                            modifier = Modifier.padding(vertical = 16.dp)
                                        )
                                    }
                                    line.trim().matches(Regex("^\\d+\\. .+")) -> {
                                        Text(
                                            text = line.trim(),
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        )
                                    }
                                    line.trim().matches(Regex("^\\d+\\.\\d+ .+")) -> {
                                        Text(
                                            text = line.trim(),
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = MaterialTheme.typography.titleSmall.fontWeight
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(top = 6.dp)
                                        )
                                    }
                                    line.isBlank() -> {
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                    else -> {
                                        Text(
                                            text = line,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            lineHeight = 20.sp,
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Barra de rolagem personalizada
                    val colorRes = if (isDarkTheme) Color(0xFF3D3D3D) else Color(0xFFABABAB)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(8.dp)
                            .background(colorRes, shape = RoundedCornerShape(4.dp))
                            .onGloballyPositioned { coordinates ->
                                containerHeightPx = coordinates.size.height
                            }
                    ) {
                        // Calcula a posição do "scroll thumb"
                        val scrollProgress = scrollState.value.toFloat() / (scrollState.maxValue.toFloat().coerceAtLeast(1f))
                        val density = LocalDensity.current
                        val availableHeight = (containerHeightPx.toFloat() - with(density) { 50.dp.toPx() }).coerceAtLeast(0f)
                        val offsetY = with(density) { (scrollProgress * availableHeight).toDp() }

                        // Animação do deslocamento vertical do thumb
                        val animatedOffsetY by animateDpAsState(
                            targetValue = offsetY,
                            label = "ScrollThumbOffset"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .offset(y = animatedOffsetY)
                                .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(4.dp))
                        )
                    }
                }
            }
        }

        // Mensagem de confirmação dos termos
        Text(
            text = "Ao continuar, você concorda com os Termos de Uso e com a nossa política de privacidade.",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Botão para aceitar os termos
        Button(
            onClick = {
                // Salva nas SharedPreferences que os termos foram aceitos
                val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                sharedPref.edit()
                    .putBoolean("tutorial_visto", true)
                    .putBoolean("termos_aceitos", true)
                    .apply()

                // Redireciona para a MainActivity e limpa a pilha de atividades
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(60.dp)
                .padding(top = 16.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "Aceito!",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
