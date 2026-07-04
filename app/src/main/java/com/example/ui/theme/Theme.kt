package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VibrantColorScheme = lightColorScheme(
    primary = AccentCyan,
    onPrimary = Color.White,
    secondary = AccentCyan,
    onSecondary = Color.White,
    tertiary = TradingRed,
    background = SlateDark,
    surface = SlateSurface,
    surfaceVariant = SlateSurfaceVariant,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = BorderSlate
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Default to false for the beautiful Vibrant Light Palette
    content: @Composable () -> Unit
) {
    val colorScheme = VibrantColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
