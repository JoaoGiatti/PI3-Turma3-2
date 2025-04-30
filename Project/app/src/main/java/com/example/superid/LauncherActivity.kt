package com.example.superid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class LauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val tutorialVisto = sharedPref.getBoolean("tutorial_visto", false)
        val termosAceitos = sharedPref.getBoolean("termos_aceitos", false)

        when {
            !tutorialVisto -> {
                startActivity(Intent(this, IntroActivity::class.java))
            }
            !termosAceitos -> {
                startActivity(Intent(this, TermsActivity::class.java))
            }
            else -> {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }

        finish() // Fecha a LauncherActivity
    }
}
