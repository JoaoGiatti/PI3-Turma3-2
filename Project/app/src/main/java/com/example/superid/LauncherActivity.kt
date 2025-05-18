package com.example.superid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.superid.ui.theme.DarkGray

class LauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Evita fundo branco momentâneo
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = DarkGray.toArgb()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        // Verifica se deve ignorar o redirecionamento automático
        val isRedirectOverride = intent.getBooleanExtra("skip_auto_redirect", false)
        if (isRedirectOverride) return

        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val tutorialVisto = sharedPref.getBoolean("tutorial_visto", false)
        val termosAceitos = sharedPref.getBoolean("termos_aceitos", false)

        when {
            !tutorialVisto -> startActivity(Intent(this, IntroActivity::class.java))
            !termosAceitos -> startActivity(Intent(this, TermsActivity::class.java))
            else -> startActivity(Intent(this, MainActivity::class.java))
        }

        finish()
    }
}
