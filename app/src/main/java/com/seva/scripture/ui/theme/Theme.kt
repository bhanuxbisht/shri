package com.seva.scripture.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Saffron,
    secondary = MutedGold,
    tertiary = Sandal,
    background = Cream,
    surface = Cream,
    onPrimary = Cream,
    onSecondary = Charcoal,
    onBackground = TextLight,
    onSurface = TextLight
)

private val DarkColors = darkColorScheme(
    primary = Sandal,
    secondary = MutedGold,
    tertiary = Saffron,
    background = DeepBrown,
    surface = Charcoal,
    onPrimary = Charcoal,
    onSecondary = DeepBrown,
    onBackground = DarkText,
    onSurface = DarkText
)

@Composable
fun ScriptureTheme(
    darkTheme: Boolean,
    fontScale: Float,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = scriptureTypography(fontScale),
        content = content
    )
}
