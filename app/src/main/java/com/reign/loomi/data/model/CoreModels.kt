package com.reign.loomi.data.model

data class RadioStation(
    val stationuuid: String,
    val name: String,
    val url_resolved: String,
)

data class ListenStatEntry(
    val name: String,
    val timeSeconds: Int,
)

data class AmbienceState(
    val active: Boolean = false,
    val volume: Int = 50,
)

data class AppSettings(
    val clickSound: Boolean = false,
)
