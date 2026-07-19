package com.uv.questionsanswers.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = IndigoDeep,
    onPrimary = Color.White,
    secondary = SuccessGreen,
    onSecondary = Color.White,
    tertiary = IndigoLight,
    background = SoftGray,
    surface = WarmWhite,
    onBackground = CharcoalDeep,
    onSurface = CharcoalDeep,
    surfaceVariant = WarmWhite,
    onSurfaceVariant = CharcoalDeep.copy(alpha = 0.6f)
)

@Composable
fun QuestionsAnswersTheme(
    content: @Composable () -> Unit
) {
    // Always use LightColorScheme
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
