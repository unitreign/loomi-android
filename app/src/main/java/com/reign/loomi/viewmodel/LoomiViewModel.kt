package com.reign.loomi.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.reign.loomi.data.local.LoomiPreferencesDataSource
import com.reign.loomi.data.model.AmbienceState
import com.reign.loomi.data.model.AppSettings
import com.reign.loomi.data.model.ListenStatEntry
import com.reign.loomi.data.model.LoomiConfig
import com.reign.loomi.data.model.PersistedSnapshot
import com.reign.loomi.data.model.RadioStation
import com.reign.loomi.data.network.RadioBrowserApi
import com.reign.loomi.data.player.AudioEngine
import com.reign.loomi.data.repository.LoomiRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class LoomiDialog {
    CHANNELS,
    TIMER,
    STATS,
    AMBIENCE,
    MIXER,
    EQUALIZER,
    THEMES,
    SETTINGS,
    ABOUT,
}

data class LoomiUiState(
    val greeting: String = "Good Evening, Listener",
    val currentStation: RadioStation? = null,
    val channelDisplayName: String = "Select a station",
    val statusText: String = "Ready",
    val isPlaying: Boolean = false,
    val lofiStations: List<RadioStation> = emptyList(),
    val otherStations: List<RadioStation> = emptyList(),
    val favoriteStations: List<RadioStation> = emptyList(),
    val userName: String = "Listener",
    val volume: Float = 0.75f,
    val musicVolume: Float = 1f,
    val ambienceVolume: Float = 0.6f,
    val ambienceStates: Map<String, AmbienceState> = emptyMap(),
    val listenStats: Map<String, ListenStatEntry> = emptyMap(),
    val sessionSeconds: Int = 0,
    val selectedTimerMinutes: Int? = null,
    val sleepTimerEndEpochMs: Long? = null,
    val sleepTimerRemainingSeconds: Int? = null,
    val albumArtUrl: String = LoomiConfig.gifUrls.first(),
    val currentThemeId: String = "default",
    val eqValues: List<Int> = listOf(0, 0, 0, 0, 0),
    val settings: AppSettings = AppSettings(),
    val activeDialog: LoomiDialog? = null,
    val isScanningStations: Boolean = false,
    val stationLoadError: String? = null,
)

class LoomiViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = LoomiRepository(
        localDataSource = LoomiPreferencesDataSource(application.applicationContext),
        api = RadioBrowserApi(),
    )

    private val audioEngine = AudioEngine(application.applicationContext)

    private val _uiState = kotlinx.coroutines.flow.MutableStateFlow(LoomiUiState())
    val uiState: kotlinx.coroutines.flow.StateFlow<LoomiUiState> = _uiState

    private var previousVolume = 0.75f
    private var stationCacheEpochMs = 0L

    private var gifJob: Job? = null
    private var sessionJob: Job? = null
    private var statsJob: Job? = null
    private var sleepTimerJob: Job? = null

    init {
        wireAudioCallbacks()
        loadInitialState()
        startSessionTimer()
        startStatsTracking()
        startRandomGifCycle()
    }

    fun openDialog(dialog: LoomiDialog) {
        playClickSoundIfEnabled()
        _uiState.value = _uiState.value.copy(activeDialog = dialog)
    }

    fun closeDialog() {
        _uiState.value = _uiState.value.copy(activeDialog = null)
    }

    fun onPlayPausePressed() {
        playClickSoundIfEnabled()
        val state = _uiState.value
        val station = state.currentStation
        if (station == null) {
            _uiState.value = state.copy(
                channelDisplayName = "Select a station first",
                statusText = "No station",
            )
            return
        }

        if (state.isPlaying) {
            audioEngine.pauseCurrent()
        } else {
            audioEngine.setStation(
                stationUrl = station.url_resolved,
                stationName = station.name,
                artworkUrl = state.albumArtUrl,
                autoPlay = true,
            )
        }
    }

    fun selectStation(station: RadioStation) {
        playClickSoundIfEnabled()
        val wasPlaying = _uiState.value.isPlaying
        _uiState.value = _uiState.value.copy(
            currentStation = station,
            channelDisplayName = station.name,
            statusText = if (wasPlaying) "Streaming" else "Ready",
            albumArtUrl = randomGif(),
            activeDialog = null,
            stationLoadError = null,
        )

        audioEngine.setStation(
            stationUrl = station.url_resolved,
            stationName = station.name,
            artworkUrl = _uiState.value.albumArtUrl,
            autoPlay = wasPlaying,
        )
        persistSnapshot()
    }

    fun toggleCurrentFavorite() {
        val station = _uiState.value.currentStation ?: return
        playClickSoundIfEnabled()
        toggleFavorite(station)
    }

    fun toggleFavorite(station: RadioStation) {
        val favorites = _uiState.value.favoriteStations.toMutableList()
        val existingIndex = favorites.indexOfFirst { it.stationuuid == station.stationuuid }
        if (existingIndex >= 0) {
            favorites.removeAt(existingIndex)
        } else {
            favorites.add(station)
        }

        _uiState.value = _uiState.value.copy(favoriteStations = favorites)
        persistSnapshot()
    }

    fun isFavorite(station: RadioStation?): Boolean {
        if (station == null) return false
        return _uiState.value.favoriteStations.any { it.stationuuid == station.stationuuid }
    }

    fun updateMasterVolume(percent: Int) {
        val normalized = (percent / 100f).coerceIn(0f, 1f)
        _uiState.value = _uiState.value.copy(volume = normalized)
        audioEngine.setMainVolumes(normalized, _uiState.value.musicVolume)
        persistSnapshot()
    }

    fun updateMusicVolume(percent: Int) {
        val normalized = (percent / 100f).coerceIn(0f, 1f)
        _uiState.value = _uiState.value.copy(musicVolume = normalized)
        audioEngine.setMainVolumes(_uiState.value.volume, normalized)
        persistSnapshot()
    }

    fun updateAmbienceVolume(percent: Int) {
        val normalized = (percent / 100f).coerceIn(0f, 1f)
        _uiState.value = _uiState.value.copy(ambienceVolume = normalized)
        audioEngine.setAmbienceGlobalVolume(_uiState.value.volume, normalized)
        persistSnapshot()
    }

    fun toggleMute() {
        playClickSoundIfEnabled()
        val currentVolume = _uiState.value.volume
        val target = if (currentVolume > 0f) {
            previousVolume = currentVolume
            0f
        } else {
            previousVolume.coerceAtLeast(0.01f)
        }

        _uiState.value = _uiState.value.copy(volume = target)
        audioEngine.setMainVolumes(target, _uiState.value.musicVolume)
        persistSnapshot()
    }

    fun toggleAmbience(trackId: String) {
        playClickSoundIfEnabled()
        val states = _uiState.value.ambienceStates.toMutableMap()
        val current = states[trackId] ?: AmbienceState()
        val updated = current.copy(active = !current.active)
        states[trackId] = updated
        _uiState.value = _uiState.value.copy(ambienceStates = states)
        audioEngine.setAmbienceState(trackId, updated)
        persistSnapshot()
    }

    fun updateAmbienceTrackVolume(trackId: String, volume: Int) {
        val clamped = volume.coerceIn(0, 100)
        val states = _uiState.value.ambienceStates.toMutableMap()
        val current = states[trackId] ?: AmbienceState()
        val updated = current.copy(volume = clamped)
        states[trackId] = updated
        _uiState.value = _uiState.value.copy(ambienceStates = states)
        audioEngine.setAmbienceState(trackId, updated)
        persistSnapshot()
    }

    fun selectTimerOption(minutes: Int) {
        playClickSoundIfEnabled()
        _uiState.value = _uiState.value.copy(selectedTimerMinutes = minutes)
    }

    fun startSleepTimer() {
        playClickSoundIfEnabled()
        val selectedMinutes = _uiState.value.selectedTimerMinutes ?: return
        val endTime = System.currentTimeMillis() + selectedMinutes * 60_000L

        sleepTimerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            sleepTimerEndEpochMs = endTime,
            activeDialog = null,
        )

        sleepTimerJob = viewModelScope.launch {
            while (isActive) {
                val remainingMs = endTime - System.currentTimeMillis()
                val remainingSeconds = (remainingMs / 1000L).toInt().coerceAtLeast(0)
                _uiState.value = _uiState.value.copy(sleepTimerRemainingSeconds = remainingSeconds)

                if (remainingMs <= 0L) {
                    audioEngine.pauseCurrent()
                    audioEngine.stopAllAmbience()

                    val clearedAmbience = _uiState.value.ambienceStates.mapValues { (_, value) ->
                        value.copy(active = false)
                    }
                    _uiState.value = _uiState.value.copy(
                        ambienceStates = clearedAmbience,
                        sleepTimerEndEpochMs = null,
                        sleepTimerRemainingSeconds = null,
                        selectedTimerMinutes = null,
                    )
                    persistSnapshot()
                    break
                }

                delay(1000)
            }
        }
    }

    fun cancelSleepTimer() {
        playClickSoundIfEnabled()
        sleepTimerJob?.cancel()
        sleepTimerJob = null
        _uiState.value = _uiState.value.copy(
            selectedTimerMinutes = null,
            sleepTimerEndEpochMs = null,
            sleepTimerRemainingSeconds = null,
        )
    }

    fun clearListenStats() {
        playClickSoundIfEnabled()
        _uiState.value = _uiState.value.copy(listenStats = emptyMap())
        persistSnapshot()
    }

    fun scanStations() {
        playClickSoundIfEnabled()
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isScanningStations = true,
                stationLoadError = null,
            )

            val result = repository.scanStations()
            result.onSuccess { scan ->
                stationCacheEpochMs = System.currentTimeMillis()
                _uiState.value = _uiState.value.copy(
                    lofiStations = scan.lofiStations,
                    otherStations = scan.otherStations,
                    isScanningStations = false,
                    stationLoadError = null,
                )
                persistSnapshot()
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isScanningStations = false,
                    stationLoadError = "Error loading stations. Please try again.",
                )
            }
        }
    }

    fun updateUserName(input: String) {
        val value = input.trim().ifBlank { "Listener" }.take(12)
        _uiState.value = _uiState.value.copy(
            userName = value,
            greeting = buildGreeting(value),
        )
        persistSnapshot()
    }

    fun setClickSoundEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            settings = _uiState.value.settings.copy(clickSound = enabled),
        )
        persistSnapshot()
    }

    fun setTheme(themeId: String) {
        playClickSoundIfEnabled()
        _uiState.value = _uiState.value.copy(currentThemeId = themeId)
        persistSnapshot()
    }

    fun updateEqBand(index: Int, value: Int) {
        val current = _uiState.value.eqValues.toMutableList()
        if (index !in current.indices) return
        current[index] = value.coerceIn(-12, 12)
        _uiState.value = _uiState.value.copy(eqValues = current)
        audioEngine.applyEqualizer(current)
        persistSnapshot()
    }

    fun applyEqPreset(presetId: String) {
        playClickSoundIfEnabled()
        val preset = LoomiConfig.eqPresets.firstOrNull { it.id == presetId } ?: return
        _uiState.value = _uiState.value.copy(eqValues = preset.values)
        audioEngine.applyEqualizer(preset.values)
        persistSnapshot()
    }

    fun resetEqualizer() {
        applyEqPreset("flat")
    }

    fun sessionDisplay(): String {
        val seconds = _uiState.value.sessionSeconds
        val minutesPart = seconds / 60
        val secondsPart = seconds % 60
        return "Session: $minutesPart:${secondsPart.toString().padStart(2, '0')}"
    }

    fun sleepTimerDisplay(): String {
        val remaining = _uiState.value.sleepTimerRemainingSeconds ?: return ""
        val mins = remaining / 60
        val secs = remaining % 60
        return "$mins:${secs.toString().padStart(2, '0')}"
    }

    fun formattedStats(): List<Pair<String, String>> {
        return _uiState.value.listenStats
            .entries
            .sortedByDescending { it.value.timeSeconds }
            .map { it.value.name to formatDuration(it.value.timeSeconds) }
    }

    fun totalListenTimeText(): String {
        val total = _uiState.value.listenStats.values.sumOf { it.timeSeconds }
        return formatDuration(total)
    }

    fun formatDuration(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60

        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m ${secs}s"
            else -> "${secs}s"
        }
    }

    override fun onCleared() {
        super.onCleared()
        gifJob?.cancel()
        sessionJob?.cancel()
        statsJob?.cancel()
        sleepTimerJob?.cancel()
        audioEngine.release()
    }

    private fun wireAudioCallbacks() {
        audioEngine.onIsPlayingChanged = { isPlaying ->
            val status = if (isPlaying) "Streaming" else "Paused"
            _uiState.value = _uiState.value.copy(
                isPlaying = isPlaying,
                statusText = status,
            )
        }

        audioEngine.onPlaybackError = {
            _uiState.value = _uiState.value.copy(
                isPlaying = false,
                statusText = "Error",
                channelDisplayName = "Stream unavailable",
            )
        }
    }

    private fun loadInitialState() {
        viewModelScope.launch {
            val snapshot = repository.loadSnapshot()
            val now = System.currentTimeMillis()
            val hasFreshCache = snapshot.cachedAtEpochMs > 0L && (now - snapshot.cachedAtEpochMs) < 24 * 60 * 60 * 1000L

            val initialState = LoomiUiState(
                greeting = buildGreeting(snapshot.userName),
                currentStation = snapshot.currentStation,
                channelDisplayName = snapshot.currentStation?.name ?: "Select a station",
                statusText = "Ready",
                isPlaying = false,
                lofiStations = if (hasFreshCache) snapshot.lofiStations else emptyList(),
                otherStations = if (hasFreshCache) snapshot.otherStations else emptyList(),
                favoriteStations = snapshot.favoriteStations,
                userName = snapshot.userName,
                volume = snapshot.volume,
                musicVolume = snapshot.musicVolume,
                ambienceVolume = snapshot.ambienceVolume,
                ambienceStates = snapshot.ambienceStates,
                listenStats = snapshot.listenStats,
                sessionSeconds = 0,
                selectedTimerMinutes = null,
                sleepTimerEndEpochMs = null,
                sleepTimerRemainingSeconds = null,
                albumArtUrl = randomGif(),
                currentThemeId = snapshot.currentThemeId,
                eqValues = snapshot.eqValues.ifEmpty { listOf(0, 0, 0, 0, 0) },
                settings = snapshot.settings,
                activeDialog = null,
                isScanningStations = false,
                stationLoadError = null,
            )

            _uiState.value = initialState
            previousVolume = initialState.volume
            stationCacheEpochMs = if (hasFreshCache) snapshot.cachedAtEpochMs else 0L

            audioEngine.setMainVolumes(initialState.volume, initialState.musicVolume)
            audioEngine.setAmbienceGlobalVolume(initialState.volume, initialState.ambienceVolume)
            audioEngine.setAmbienceStates(initialState.ambienceStates)
            audioEngine.applyEqualizer(initialState.eqValues)
            initialState.currentStation?.let { station ->
                audioEngine.setStation(
                    stationUrl = station.url_resolved,
                    stationName = station.name,
                    artworkUrl = initialState.albumArtUrl,
                    autoPlay = false,
                )
            }
        }
    }

    private fun startRandomGifCycle() {
        gifJob?.cancel()
        gifJob = viewModelScope.launch {
            while (isActive) {
                val minutes = Random.nextInt(from = 3, until = 6)
                delay(minutes * 60_000L)
                _uiState.value = _uiState.value.copy(albumArtUrl = randomGif())
            }
        }
    }

    private fun startSessionTimer() {
        sessionJob?.cancel()
        sessionJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                val current = _uiState.value
                if (!current.isPlaying) continue
                _uiState.value = current.copy(sessionSeconds = current.sessionSeconds + 1)
            }
        }
    }

    private fun startStatsTracking() {
        statsJob?.cancel()
        statsJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)

                val current = _uiState.value
                val station = current.currentStation
                if (!current.isPlaying || station == null) {
                    continue
                }

                val updatedStats = current.listenStats.toMutableMap()
                val entry = updatedStats[station.stationuuid]
                val updatedTime = (entry?.timeSeconds ?: 0) + 1
                updatedStats[station.stationuuid] = ListenStatEntry(
                    name = station.name,
                    timeSeconds = updatedTime,
                )

                _uiState.value = current.copy(listenStats = updatedStats)

                if (updatedTime % 30 == 0) {
                    persistSnapshot()
                }
            }
        }
    }

    private fun persistSnapshot() {
        val state = _uiState.value
        val snapshot = PersistedSnapshot(
            favoriteStations = state.favoriteStations,
            currentStation = state.currentStation,
            lofiStations = state.lofiStations,
            otherStations = state.otherStations,
            cachedAtEpochMs = stationCacheEpochMs,
            userName = state.userName,
            volume = state.volume,
            musicVolume = state.musicVolume,
            ambienceVolume = state.ambienceVolume,
            ambienceStates = state.ambienceStates,
            listenStats = state.listenStats,
            currentThemeId = state.currentThemeId,
            eqValues = state.eqValues,
            settings = state.settings,
        )

        viewModelScope.launch {
            repository.saveSnapshot(snapshot)
        }
    }

    private fun randomGif(): String {
        return LoomiConfig.gifUrls.random()
    }

    private fun playClickSoundIfEnabled() {
        // Click sounds removed from native app settings.
    }

    private fun buildGreeting(userName: String): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour in 5..7 -> "Early Bird"
            hour in 8..11 -> "Good Morning"
            hour in 12..13 -> "Good Afternoon"
            hour in 14..16 -> "Hey There"
            hour in 17..19 -> "Good Evening"
            hour in 20..22 -> "Night Owl"
            hour == 23 || hour == 0 -> "Late Night"
            hour in 1..2 -> "Still Up"
            else -> "Can't Sleep"
        }

        return "$greeting, $userName"
    }
}
