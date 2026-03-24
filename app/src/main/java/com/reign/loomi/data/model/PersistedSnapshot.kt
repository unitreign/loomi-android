package com.reign.loomi.data.model

data class PersistedSnapshot(
    val favoriteStations: List<RadioStation> = emptyList(),
    val currentStation: RadioStation? = null,
    val lofiStations: List<RadioStation> = emptyList(),
    val otherStations: List<RadioStation> = emptyList(),
    val cachedAtEpochMs: Long = 0L,
    val userName: String = "Listener",
    val volume: Float = 0.75f,
    val musicVolume: Float = 1f,
    val ambienceVolume: Float = 0.6f,
    val ambienceStates: Map<String, AmbienceState> = emptyMap(),
    val listenStats: Map<String, ListenStatEntry> = emptyMap(),
    val currentThemeId: String = "default",
    val eqValues: List<Int> = listOf(0, 0, 0, 0, 0),
    val settings: AppSettings = AppSettings(),
)
