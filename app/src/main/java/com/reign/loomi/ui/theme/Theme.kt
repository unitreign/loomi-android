package com.reign.loomi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

@Composable
fun LoomiTheme(
    themeId: String,
    content: @Composable () -> Unit,
) {
    val colors = themeColorsFor(themeId)

    val colorScheme = darkColorScheme(
        primary = colors.accent,
        onPrimary = colors.textPrimary,
        secondary = colors.accentDim,
        onSecondary = colors.textPrimary,
        tertiary = colors.accent,
        background = colors.bgPrimary,
        onBackground = colors.textPrimary,
        surface = colors.bgSecondary,
        onSurface = colors.textPrimary,
        surfaceVariant = colors.bgTertiary,
        onSurfaceVariant = colors.textSecondary,
        outline = colors.border,
        error = colors.accent,
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
