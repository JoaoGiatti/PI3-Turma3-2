package com.example.superid.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.platform.LocalContext


private val DarkColors = darkColorScheme(
    primary = Yellow,
    onPrimary = Black,
    secondary = Gray,
    onSecondary = White,
    background = DarkGray,
    onBackground = White,
    surface = DarkGray,
    surfaceVariant= LightGray,
    onSurface = White,
    error = ErrorRed,
    onError = White
)

private val LightColors = lightColorScheme(
    primary = Yellow,
    onPrimary = DarkGray,
    secondary = DarkGray,
    onSecondary = White,
    background = White,
    onBackground = DarkGray,
    tertiary = Black,
    surface = White,
    surfaceVariant= LightGray,
    onSurface = DarkGray ,
    error = ErrorRed,
    onError = White
)

@Composable
fun SuperIDTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // desabilita cores dinâmicas para respeitar seu esquema
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // ou seu Typography customizado
        content = content
    )
}
