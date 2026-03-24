package com.reign.loomi.ui.theme

import androidx.compose.ui.graphics.Color
import com.reign.loomi.data.model.LoomiConfig
import com.reign.loomi.data.model.ThemePreset

data class LoomiThemeColors(
    val bgPrimary: Color,
    val bgSecondary: Color,
    val bgTertiary: Color,
    val accent: Color,
    val accentDim: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
)

fun themeColorsFor(themeId: String): LoomiThemeColors {
    val preset: ThemePreset = LoomiConfig.themeById(themeId)
    val c = preset.colors

    return LoomiThemeColors(
        bgPrimary = Color(c.bgPrimary),
        bgSecondary = Color(c.bgSecondary),
        bgTertiary = Color(c.bgTertiary),
        accent = Color(c.accent),
        accentDim = Color(c.accentDim),
        textPrimary = Color(c.textPrimary),
        textSecondary = Color(c.textSecondary),
        border = Color(c.border),
    )
}
