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

class TermsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val termos = resources.openRawResource(R.raw.termos)
            .bufferedReader().use { it.readText() }

        setContent {
            SuperIDTheme {
                TermsScreen(termos)
            }
        }
    }
}

@Composable
fun TermsScreen(termosText: String) {
    val scrollState = rememberScrollState()
    var containerHeightPx by remember { mutableStateOf(0) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.superidlogowhiteyellow),
            contentDescription = "Logo SuperID",
            modifier = Modifier
                .size(100.dp)
        )

        Text(
            text = "Termos de Uso e Privacidade",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.70f)
        ) {
            Surface(
                modifier = Modifier.matchParentSize(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 4.dp
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
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
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = MaterialTheme.typography.titleSmall.fontWeight),
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

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(8.dp)
                            .background(Color(0xFF555555), shape = RoundedCornerShape(4.dp))
                            .onGloballyPositioned { coordinates ->
                                containerHeightPx = coordinates.size.height
                            }
                    ) {
                        val scrollProgress = scrollState.value.toFloat() / (scrollState.maxValue.toFloat().coerceAtLeast(1f))
                        val density = LocalDensity.current
                        val availableHeight = (containerHeightPx.toFloat() - with(density) { 50.dp.toPx() }).coerceAtLeast(0f)
                        val offsetY = with(density) { (scrollProgress * availableHeight).toDp() }

                        val animatedOffsetY by animateDpAsState(targetValue = offsetY, label = "ScrollThumbOffset")

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

        Text(
            text = "Ao continuar, você concorda com os Termos de Uso e com a nossa política de privacidade.",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Button(
            onClick = {
                val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                sharedPref.edit()
                    .putBoolean("tutorial_visto", true)
                    .putBoolean("termos_aceitos", true)
                    .apply()

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
