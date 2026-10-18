package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NoiseDarkColorScheme = darkColorScheme(
    primary = NeonRose,
    onPrimary = Color.White,
    primaryContainer = NeonRoseDeep,
    onPrimaryContainer = Color.White,
    secondary = ElectricPurple,
    onSecondary = Color.White,
    secondaryContainer = ElectricViolet,
    onSecondaryContainer = Color.White,
    tertiary = CoinGold,
    onTertiary = ObsidianBlack,
    background = ObsidianBlack,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = DarkBorder
)

@Composable
fun NoiseTheme(
    darkTheme: Boolean = true, // Noise is intentionally a dark luxury experience
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NoiseDarkColorScheme,
        typography = Typography,
        content = content
    )
}
