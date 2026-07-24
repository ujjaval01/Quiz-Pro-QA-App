package com.uv.questionsanswers.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4A90E2),
    onPrimary = Color.White,
    secondary = Color(0xFF50C878),
    onSecondary = Color.White,
    tertiary = IndigoLight,
    background = NeuBackground,
    surface = NeuBackground,
    onBackground = CharcoalDeep,
    onSurface = CharcoalDeep,
    surfaceVariant = NeuDarkShadow.copy(alpha = 0.5f),
    onSurfaceVariant = CharcoalDeep.copy(alpha = 0.6f)
)

@Composable
fun QuestionsAnswersTheme(
    content: @Composable () -> Unit
) {
    // Always use LightColorScheme with Neumorphic colors
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
