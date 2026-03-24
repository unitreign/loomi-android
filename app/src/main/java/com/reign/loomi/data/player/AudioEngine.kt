package com.reign.loomi.data.player

import android.content.Context
import android.media.MediaPlayer
import android.media.AudioAttributes as AndroidAudioAttributes
import android.media.audiofx.Equalizer
import android.net.Uri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.reign.loomi.data.model.AmbienceState
import com.reign.loomi.data.model.LoomiConfig
import kotlin.math.min
import kotlin.math.roundToInt

class AudioEngine(context: Context) {
    private val appContext = context.applicationContext

    private val player = ExoPlayer.Builder(appContext).build().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(androidx.media3.common.C.USAGE_MEDIA)
                .build(),
            true,
        )
    }
    private val mediaSession = MediaSession.Builder(appContext, player).build()

    private val ambiencePlayers = LoomiConfig.ambienceTracks.associate { track ->
        track.id to MediaPlayer.create(appContext, track.rawResId).apply {
            isLooping = true
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                setAudioAttributes(
                    AndroidAudioAttributes.Builder()
                        .setUsage(AndroidAudioAttributes.USAGE_MEDIA)
                        .setContentType(AndroidAudioAttributes.CONTENT_TYPE_MUSIC)
                        .build(),
                )
            }
            setVolume(0f, 0f)
        }
    }.toMutableMap()

    private var currentStationUrl: String? = null
    private var currentStationName: String? = null
    private var currentArtworkUrl: String? = null
    private var masterVolume: Float = 0.75f
    private var musicVolume: Float = 1f
    private var ambienceVolume: Float = 0.6f
    private var ambienceStates: Map<String, AmbienceState> = emptyMap()
    private var eqValues: List<Int> = listOf(0, 0, 0, 0, 0)

    private var equalizer: Equalizer? = null

    var onIsPlayingChanged: ((Boolean) -> Unit)? = null
    var onPlaybackError: ((String) -> Unit)? = null

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (isPlaying) {
                    resumeActiveAmbience()
                } else {
                    pauseActiveAmbience()
                }
                onIsPlayingChanged?.invoke(isPlaying)
            }

            override fun onPlayerError(error: PlaybackException) {
                onPlaybackError?.invoke(error.errorCodeName)
            }

            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                attachEqualizer(audioSessionId)
            }
        })
    }

    fun setStation(
        stationUrl: String,
        stationName: String,
        artworkUrl: String?,
        autoPlay: Boolean,
    ) {
        val needsReload = currentStationUrl != stationUrl
        val metadataChanged = currentStationName != stationName || currentArtworkUrl != artworkUrl
        currentStationUrl = stationUrl
        currentStationName = stationName
        currentArtworkUrl = artworkUrl
        val item = buildStationMediaItem(stationUrl, stationName, artworkUrl)

        if (needsReload) {
            player.setMediaItem(item)
            player.prepare()
        } else if (metadataChanged && player.currentMediaItemIndex >= 0) {
            player.replaceMediaItem(player.currentMediaItemIndex, item)
        }

        if (autoPlay) {
            player.playWhenReady = true
            player.play()
        } else {
            player.playWhenReady = false
        }

        updateMainVolume()
    }

    fun playCurrent() {
        if (currentStationUrl == null) return
        player.playWhenReady = true
        player.play()
    }

    fun pauseCurrent() {
        player.pause()
    }

    fun setMainVolumes(master: Float, music: Float) {
        masterVolume = master
        musicVolume = music
        updateMainVolume()
        updateAmbienceVolumes()
    }

    fun setAmbienceGlobalVolume(master: Float, ambience: Float) {
        masterVolume = master
        ambienceVolume = ambience
        updateAmbienceVolumes()
    }

    fun setAmbienceStates(states: Map<String, AmbienceState>) {
        ambienceStates = states
        // Restored ambience state should stay silent until user plays audio again.
        syncAmbiencePlaybackWithState(startActiveTracks = player.isPlaying)
        updateAmbienceVolumes()
    }

    fun setAmbienceState(trackId: String, state: AmbienceState) {
        ambienceStates = ambienceStates.toMutableMap().apply { put(trackId, state) }
        // User toggles should be audible immediately.
        syncSingleAmbience(trackId, state, startIfActive = true)
    }

    fun stopAllAmbience() {
        ambiencePlayers.values.forEach { mediaPlayer ->
            runCatching {
                mediaPlayer.pause()
                mediaPlayer.seekTo(0)
            }
        }
    }

    fun applyEqualizer(values: List<Int>) {
        eqValues = values
        applyEqValuesToEngine()
    }

    fun isPlaying(): Boolean = player.isPlaying

    fun release() {
        equalizer?.release()
        equalizer = null
        mediaSession.release()
        player.release()
        ambiencePlayers.values.forEach { mediaPlayer ->
            runCatching {
                mediaPlayer.stop()
                mediaPlayer.release()
            }
        }
    }

    private fun updateMainVolume() {
        player.volume = (masterVolume * musicVolume).coerceIn(0f, 1f)
    }

    private fun updateAmbienceVolumes() {
        ambienceStates.forEach { (id, state) ->
            val player = ambiencePlayers[id] ?: return@forEach
            if (!state.active) {
                player.setVolume(0f, 0f)
                return@forEach
            }
            val localLevel = (state.volume / 100f).coerceIn(0f, 1f)
            val volume = (masterVolume * ambienceVolume * localLevel).coerceIn(0f, 1f)
            player.setVolume(volume, volume)
        }
    }

    private fun syncAmbiencePlaybackWithState(startActiveTracks: Boolean) {
        ambienceStates.forEach { (id, state) ->
            syncSingleAmbience(id, state, startIfActive = startActiveTracks)
        }

        // Stop tracks that are no longer in state.
        ambiencePlayers.keys
            .filter { it !in ambienceStates.keys }
            .forEach { id ->
                val mediaPlayer = ambiencePlayers[id] ?: return@forEach
                runCatching {
                    mediaPlayer.pause()
                    mediaPlayer.seekTo(0)
                }
            }
    }

    private fun syncSingleAmbience(trackId: String, state: AmbienceState, startIfActive: Boolean) {
        val mediaPlayer = ambiencePlayers[trackId] ?: return
        if (state.active) {
            if (startIfActive) {
                runCatching { mediaPlayer.start() }
            }
        } else {
            runCatching {
                mediaPlayer.pause()
                mediaPlayer.seekTo(0)
            }
        }
        updateAmbienceVolumes()
    }

    private fun pauseActiveAmbience() {
        ambienceStates.forEach { (id, state) ->
            if (!state.active) return@forEach
            ambiencePlayers[id]?.let { mediaPlayer ->
                runCatching { mediaPlayer.pause() }
            }
        }
    }

    private fun resumeActiveAmbience() {
        ambienceStates.forEach { (id, state) ->
            if (!state.active) return@forEach
            ambiencePlayers[id]?.let { mediaPlayer ->
                runCatching {
                    mediaPlayer.start()
                }
            }
        }
    }

    private fun attachEqualizer(audioSessionId: Int) {
        if (audioSessionId <= 0) return

        runCatching {
            equalizer?.release()
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = true
            }
            applyEqValuesToEngine()
        }
    }

    private fun applyEqValuesToEngine() {
        val eq = equalizer ?: return
        val range = eq.bandLevelRange
        val minLevel = range[0].toInt()
        val maxLevel = range[1].toInt()
        val maxUiDb = 12f
        val positiveHeadroom = maxLevel.coerceAtLeast(0)
        val negativeHeadroom = (-minLevel).coerceAtLeast(0)

        val bandCount = min(eq.numberOfBands.toInt(), eqValues.size)
        for (index in 0 until bandCount) {
            val uiDb = eqValues[index].coerceIn(-12, 12)
            val scaledLevelMb = if (uiDb >= 0) {
                ((uiDb / maxUiDb) * positiveHeadroom).roundToInt()
            } else {
                -(((-uiDb) / maxUiDb) * negativeHeadroom).roundToInt()
            }
            val levelMb = scaledLevelMb.coerceIn(minLevel, maxLevel)
            runCatching {
                eq.setBandLevel(index.toShort(), levelMb.toShort())
            }
        }
    }

    private fun buildStationMediaItem(
        stationUrl: String,
        stationName: String,
        artworkUrl: String?,
    ): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(stationName)
            .setArtist("Loomi")
            .setAlbumTitle("Loomi Radio")
            .apply {
                if (!artworkUrl.isNullOrBlank()) {
                    setArtworkUri(Uri.parse(artworkUrl))
                }
            }
            .build()

        return MediaItem.Builder()
            .setUri(stationUrl)
            .setMediaMetadata(metadata)
            .build()
    }
}
