package com.example.superid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
    Text("Tela de login aaaaaaa")
}