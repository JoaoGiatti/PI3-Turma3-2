package com.example.superid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.toSize

class TermsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val termos = resources.openRawResource(R.raw.termos)
            .bufferedReader().use { it.readText() }

        setContent {
            TermsScreen(termos)
        }
    }
}

@Composable
fun TermsScreen(termosText: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF131313))
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Image(
            painter = painterResource(id = R.drawable.superidlogowhiteyellow),
            contentDescription = "Logo SuperID",
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 0.dp)
        )

        // --- Título "Termos de Uso" ---
        Text(
            text = "Termos de Uso e Privacidade",
            color = Color.White,
            fontSize = 20.sp,  //
            fontFamily = FontFamily(Font(R.font.poppinsbold)),
            modifier = Modifier
                .padding(bottom = 16.dp)
        )

        val scrollState = rememberScrollState()
        var containerHeightPx by remember { mutableStateOf(0) }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.70f)
        ) {
            Surface(
                modifier = Modifier.matchParentSize(),
                color = Color(0xFF323232),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
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
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                                        )
                                    }
                                    line.trim().matches(Regex("^\\d+\\.\\d+ .+")) -> {
                                        Text(
                                            text = line.trim(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(top = 8.dp)
                                        )
                                    }
                                    line.isBlank() -> {
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                    else -> {
                                        Text(
                                            text = line,
                                            fontSize = 14.sp,
                                            fontFamily = FontFamily(Font(R.font.interregular)),
                                            color = Color.White,
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
                        val offsetY = with(density) {
                            (scrollProgress * availableHeight).toDp()
                        }

                        val animatedOffsetY by animateDpAsState(targetValue = offsetY, label = "ScrollThumbOffset")

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .offset(y = animatedOffsetY)
                                .background(Color(0xFFE2DA06), shape = RoundedCornerShape(4.dp))
                        )
                    }
                }
            }
        }


        Text(
            text = "Ao continuar, você concorda com os Termos de Uso e com a nossa política de privacidade.",
            color = Color.Gray,
            fontSize = 12.sp,
            fontFamily = FontFamily(Font(R.font.interregular)),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        val context = LocalContext.current
        Button(
            onClick = {
                val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                sharedPref.edit()
                    .putBoolean("termos_aceitos", true)
                    .apply()

                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2DA06)),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(60.dp)
                .padding(top = 16.dp),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text(
                text = "Aceito!",
                color = Color.Black,
                fontSize = 16.sp,
                fontFamily = FontFamily(Font(R.font.interbold))

            )
        }
    }
}






