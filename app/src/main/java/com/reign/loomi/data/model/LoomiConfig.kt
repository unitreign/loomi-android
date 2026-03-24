package com.reign.loomi.data.model

import com.reign.loomi.R

data class AmbienceTrack(
    val id: String,
    val name: String,
    val fileName: String,
    val rawResId: Int,
)

data class ThemeColors(
    val bgPrimary: Long,
    val bgSecondary: Long,
    val bgTertiary: Long,
    val accent: Long,
    val accentDim: Long,
    val textPrimary: Long,
    val textSecondary: Long,
    val border: Long,
)

data class ThemePreset(
    val id: String,
    val name: String,
    val imageUrl: String,
    val colors: ThemeColors,
)

data class EqBand(
    val freq: Int,
    val label: String,
)

data class EqPreset(
    val id: String,
    val name: String,
    val values: List<Int>,
)

object LoomiConfig {
    val apiServers = listOf(
        "https://de1.api.radio-browser.info",
        "https://de2.api.radio-browser.info",
        "https://fi1.api.radio-browser.info",
    )

    val stationTags = listOf("lofi", "synthwave", "chillout")

    val gifUrls = listOf(
        "https://media1.tenor.com/m/uvC1Vj7ooUUAAAAC/cat-not-mine.gif",
        "https://media1.tenor.com/m/G8Bgyj10assAAAAC/lofi.gif",
        "https://media1.tenor.com/m/Mqku-ywxNEIAAAAd/kagome-inuyasha.gif",
        "https://media1.tenor.com/m/wIa91mot0tAAAAAd/pixel-city-chill.gif",
        "https://media1.tenor.com/m/i-a_gLyG6fAAAAAd/pixel-art-rain.gif",
        "https://media1.tenor.com/m/grQ0hRksizwAAAAC/chill.gif",
        "https://media1.tenor.com/m/gVyw1Ba60kMAAAAd/o9iw-city-nights.gif",
        "https://media1.tenor.com/m/h-vRtS8s5roAAAAC/sad-night.gif",
        "https://media1.tenor.com/m/ubkgsEHmfe4AAAAC/anime-aesthetic.gif",
    )

    val timerOptionsMinutes = listOf(10, 15, 20, 30, 45, 60, 90, 120)

    val ambienceTracks = listOf(
        AmbienceTrack("rain1", "Soft Rain", "rain1.mp3", R.raw.rain1),
        AmbienceTrack("rain2", "Rain", "rain2.mp3", R.raw.rain2),
        AmbienceTrack("cafe", "Cafe", "cafe.mp3", R.raw.cafe),
        AmbienceTrack("ocean", "Ocean Waves", "ocean.mp3", R.raw.ocean),
        AmbienceTrack("nature", "Birds", "nature.mp3", R.raw.nature),
        AmbienceTrack("night", "Crickets", "night.mp3", R.raw.night),
        AmbienceTrack("street", "City Street", "street.mp3", R.raw.street),
        AmbienceTrack("typing", "Library Typing", "typing.mp3", R.raw.typing),
    )

    val eqBands = listOf(
        EqBand(60, "60"),
        EqBand(250, "250"),
        EqBand(1000, "1K"),
        EqBand(4000, "4K"),
        EqBand(16000, "16K"),
    )

    val eqPresets = listOf(
        EqPreset("flat", "Flat", listOf(0, 0, 0, 0, 0)),
        EqPreset("bass", "Bass Boost", listOf(6, 4, 0, 0, 0)),
        EqPreset("treble", "Treble Boost", listOf(0, 0, 0, 4, 6)),
        EqPreset("vocal", "Vocal", listOf(-2, 0, 4, 2, 0)),
        EqPreset("lofi", "Lofi", listOf(3, 1, -1, 2, 4)),
        EqPreset("deep", "Deep", listOf(5, 3, 0, -1, -2)),
    )

    val themes = listOf(
        ThemePreset(
            id = "default",
            name = "Default",
            imageUrl = "https://media1.tenor.com/m/wIa91mot0tAAAAAd/pixel-city-chill.gif",
            colors = ThemeColors(
                bgPrimary = 0xFF1A1A2EL,
                bgSecondary = 0xFF16213EL,
                bgTertiary = 0xFF0F0F23L,
                accent = 0xFFE94560L,
                accentDim = 0xFFA63446L,
                textPrimary = 0xFFEAEAEAL,
                textSecondary = 0xFFA0A0A0L,
                border = 0xFF3A3A5CL,
            ),
        ),
        ThemePreset(
            id = "ocean",
            name = "Ocean",
            imageUrl = "https://media1.tenor.com/m/UWU5vh3QAJcAAAAd/ocean-themed-ocean-themed-discord.gif",
            colors = ThemeColors(
                bgPrimary = 0xFF0A1628L,
                bgSecondary = 0xFF0D2137L,
                bgTertiary = 0xFF061220L,
                accent = 0xFF4FC3DCL,
                accentDim = 0xFF2D8A9EL,
                textPrimary = 0xFFE0F4F8L,
                textSecondary = 0xFF7EB8C9L,
                border = 0xFF1E4A5CL,
            ),
        ),
        ThemePreset(
            id = "nature",
            name = "Nature",
            imageUrl = "https://media1.tenor.com/m/KPWz5Ick0WUAAAAd/nature-false.gif",
            colors = ThemeColors(
                bgPrimary = 0xFF1A2E1AL,
                bgSecondary = 0xFF243524L,
                bgTertiary = 0xFF0F1F0FL,
                accent = 0xFF6BCF6BL,
                accentDim = 0xFF4A9E4AL,
                textPrimary = 0xFFE8F5E8L,
                textSecondary = 0xFFA0C4A0L,
                border = 0xFF3A5C3AL,
            ),
        ),
        ThemePreset(
            id = "sakura",
            name = "Sakura",
            imageUrl = "https://media1.tenor.com/m/9pA2BH1YT_8AAAAd/lofi.gif",
            colors = ThemeColors(
                bgPrimary = 0xFF2E1A2AL,
                bgSecondary = 0xFF3D2438L,
                bgTertiary = 0xFF1F0F1CL,
                accent = 0xFFF7A8C4L,
                accentDim = 0xFFD47A9EL,
                textPrimary = 0xFFFCE8F0L,
                textSecondary = 0xFFC9A0B8L,
                border = 0xFF5C3A52L,
            ),
        ),
        ThemePreset(
            id = "cafe",
            name = "Cafe",
            imageUrl = "https://media1.tenor.com/m/IudIKz2q7YAAAAAd/lofi.gif",
            colors = ThemeColors(
                bgPrimary = 0xFF1A1520L,
                bgSecondary = 0xFF252030L,
                bgTertiary = 0xFF0F0C14L,
                accent = 0xFF7BA3C9L,
                accentDim = 0xFF5A7D9EL,
                textPrimary = 0xFFE0E4EAL,
                textSecondary = 0xFF9099A8L,
                border = 0xFF3A3F50L,
            ),
        ),
        ThemePreset(
            id = "cozy",
            name = "Cozy",
            imageUrl = "https://media1.tenor.com/m/uvC1Vj7ooUUAAAAd/cat-not-mine.gif",
            colors = ThemeColors(
                bgPrimary = 0xFF1E1828L,
                bgSecondary = 0xFF2A2235L,
                bgTertiary = 0xFF14101CL,
                accent = 0xFFC9A0DCL,
                accentDim = 0xFF9E7AAEL,
                textPrimary = 0xFFF0E8F5L,
                textSecondary = 0xFFB0A0BBL,
                border = 0xFF4A3D5CL,
            ),
        ),
        ThemePreset(
            id = "sunset",
            name = "Sunset",
            imageUrl = "https://media1.tenor.com/m/R6uaKtUlsm4AAAAC/good-afternoon-lofi.gif",
            colors = ThemeColors(
                bgPrimary = 0xFF1F1A14L,
                bgSecondary = 0xFF2A2318L,
                bgTertiary = 0xFF14110CL,
                accent = 0xFFF0A830L,
                accentDim = 0xFFC48820L,
                textPrimary = 0xFFF5EFE6L,
                textSecondary = 0xFFBFAB8FL,
                border = 0xFF4A3D2AL,
            ),
        ),
        ThemePreset(
            id = "rose",
            name = "Rose",
            imageUrl = "https://media1.tenor.com/m/9YhOMMynP2IAAAAd/red-aesthetic.gif",
            colors = ThemeColors(
                bgPrimary = 0xFF1E1214L,
                bgSecondary = 0xFF2A1A1CL,
                bgTertiary = 0xFF140D0EL,
                accent = 0xFFF36063L,
                accentDim = 0xFFC04A4DL,
                textPrimary = 0xFFF8E8E9L,
                textSecondary = 0xFFC9A0A2L,
                border = 0xFF4A2A2CL,
            ),
        ),
    )

    fun themeById(themeId: String): ThemePreset = themes.firstOrNull { it.id == themeId } ?: themes.first()
}
