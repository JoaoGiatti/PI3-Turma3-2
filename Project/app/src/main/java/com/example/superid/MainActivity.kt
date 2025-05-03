package com.example.superid

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.ui.theme.SuperIDTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperIDTheme {
                SuperIDScreen()
            }
        }
    }
}

@Composable
fun SuperIDScreen() {
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.superidlogowhiteyellow),
            contentDescription = "Logo SuperID",
            modifier = Modifier
                .height(24.dp)
                .offset { IntOffset(x = 0, y = -180) }
        )

        Spacer(modifier = Modifier.height(100.dp))

        Image(
            painter = painterResource(id = R.drawable.walletvector),
            contentDescription = "Wallet Icon",
            modifier = Modifier.size(180.dp)
        )

        Spacer(modifier = Modifier.height(46.dp))

        Text(
            text = buildAnnotatedString {
                append("Um modo inovador de fazer login ")
                pushStyle(SpanStyle(color = colors.primary))
                append("sem usar senhas")
                pop()
                append(".")
            },
            style = typography.titleMedium.copy(
                color = colors.onBackground,
                textAlign = TextAlign.Start,
                lineHeight = 30.sp
            ),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(horizontal = 10.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                context.startActivity(Intent(context, LogInActivity::class.java))
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primary,
                contentColor = colors.onPrimary
            ),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(42.dp),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(
                text = "Entrar na conta",
                style = typography.labelMedium.copy(color = colors.onPrimary)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedButton(
            onClick = {
                context.startActivity(Intent(context, SignInActivity::class.java))
            },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = colors.onBackground
            ),
            border = ButtonDefaults.outlinedButtonBorder,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(42.dp),
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(
                text = "Registrar",
                style = typography.labelMedium.copy(color = colors.onBackground)
            )
        }
    }
}
