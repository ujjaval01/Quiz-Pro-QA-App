package com.uv.questionsanswers.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = IndigoLight,
    onPrimary = WarmWhite,

    secondary = EmeraldAccent,
    onSecondary = WarmWhite,

    tertiary = IndigoDeep,

    background = NeuBackground,
    onBackground = CharcoalDeep,

    surface = NeuBackground,
    onSurface = CharcoalDeep,

    surfaceVariant = NeuDarkShadow.copy(alpha = 0.5f),
    onSurfaceVariant = CharcoalDeep.copy(alpha = 0.7f)
)

private val DarkColorScheme = darkColorScheme(
    primary = IndigoLight,
    onPrimary = WarmWhite,

    secondary = EmeraldAccent,
    onSecondary = WarmWhite,

    tertiary = IndigoDeep,

    background = DarkBackground,
    onBackground = DarkOnSurface,

    surface = DarkSurface,
    onSurface = DarkOnSurface,

    surfaceVariant = DarkSurface,
    onSurfaceVariant = DarkOnSurface.copy(alpha = 0.7f)
)

@Composable
fun QuestionsAnswersTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}