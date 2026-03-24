package com.reign.loomi.ui.screen

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.reign.loomi.data.model.AmbienceState
import com.reign.loomi.data.model.AmbienceTrack
import com.reign.loomi.data.model.LoomiConfig
import com.reign.loomi.data.model.RadioStation
import com.reign.loomi.ui.theme.LoomiFonts
import com.reign.loomi.viewmodel.LoomiDialog
import com.reign.loomi.viewmodel.LoomiUiState
import com.reign.loomi.viewmodel.LoomiViewModel
import kotlin.math.roundToInt

private val RetroCardShape = RoundedCornerShape(3.dp)

@Composable
fun HomeRoute(viewModel: LoomiViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(uiState = uiState, viewModel = viewModel)
}

@Composable
private fun HomeScreen(
    uiState: LoomiUiState,
    viewModel: LoomiViewModel,
) {
    val colors = MaterialTheme.colorScheme
    val gifImageLoader = rememberGifImageLoader()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colors.background,
                        colors.surface,
                        colors.background,
                    ),
                ),
            )
            .drawWithContent {
                drawContent()
                val scanline = Color.Black.copy(alpha = 0.10f)
                var y = 0f
                while (y < size.height) {
                    drawRect(
                        color = scanline,
                        topLeft = Offset(0f, y),
                        size = Size(size.width, 1.2f),
                    )
                    y += 6f
                }
            }
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            RetroFrame(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp)
                .wrapContentHeight(),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = uiState.greeting,
                        color = colors.onBackground,
                        style = retroTitleStyle(),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    RetroPanel {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AsyncImage(
                                model = uiState.albumArtUrl,
                                imageLoader = gifImageLoader,
                                contentDescription = "Album art",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(82.dp)
                                    .border(2.dp, colors.outline, RetroCardShape)
                                    .clip(RetroCardShape),
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "NOW PLAYING",
                                    color = colors.onSurfaceVariant,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp,
                                    fontFamily = LoomiFonts.Pixel,
                                )
                                Text(
                                    text = uiState.channelDisplayName,
                                    color = colors.primary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    style = TextStyle(
                                        fontFamily = LoomiFonts.Pixel,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp,
                                    ),
                                )
                                Row(
                                    modifier = Modifier.padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                statusColor(
                                                    status = uiState.statusText,
                                                    playingColor = colors.primary,
                                                    errorColor = colors.error,
                                                    idleColor = colors.onSurfaceVariant,
                                                ),
                                            ),
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = uiState.statusText.uppercase(),
                                        color = colors.onSurfaceVariant,
                                        fontSize = 11.sp,
                                        fontFamily = LoomiFonts.Body,
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RetroIconButton(
                            icon = if (viewModel.isFavorite(uiState.currentStation)) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            active = viewModel.isFavorite(uiState.currentStation),
                            onClick = { viewModel.toggleCurrentFavorite() },
                        )
                        RetroIconButton(
                            icon = Icons.Filled.Wifi,
                            onClick = {
                                viewModel.openDialog(LoomiDialog.CHANNELS)
                                if (uiState.lofiStations.isEmpty() && uiState.otherStations.isEmpty() && !uiState.isScanningStations) {
                                    viewModel.scanStations()
                                }
                            },
                        )
                        RetroPlayButton(
                            isPlaying = uiState.isPlaying,
                            onClick = { viewModel.onPlayPausePressed() },
                        )
                        RetroIconButton(
                            icon = Icons.Filled.Timer,
                            onClick = { viewModel.openDialog(LoomiDialog.TIMER) },
                        )
                        RetroIconButton(
                            icon = Icons.Filled.Equalizer,
                            onClick = { viewModel.openDialog(LoomiDialog.STATS) },
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    RetroPanel {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RetroIconButton(
                                icon = when {
                                    uiState.volume <= 0f -> Icons.Filled.VolumeMute
                                    uiState.volume < 0.5f -> Icons.Filled.VolumeDown
                                    else -> Icons.Filled.VolumeUp
                                },
                                compact = true,
                                onClick = { viewModel.toggleMute() },
                            )
                            RetroVolumeBar(
                                value = uiState.volume,
                                onValueChange = { viewModel.updateMasterVolume((it * 100).roundToInt()) },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 6.dp),
                            )
                            Text(
                                text = "${(uiState.volume * 100).toInt()}%",
                                color = colors.onSurface,
                                fontFamily = LoomiFonts.Body,
                                fontSize = 12.sp,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val actionButtons = listOf(
                        ActionItem("AMBIENCE", Icons.Filled.Waves) { viewModel.openDialog(LoomiDialog.AMBIENCE) },
                        ActionItem("MIXER", Icons.Filled.Tune) { viewModel.openDialog(LoomiDialog.MIXER) },
                        ActionItem("EQUALIZER", Icons.Filled.GraphicEq) { viewModel.openDialog(LoomiDialog.EQUALIZER) },
                        ActionItem("THEMES", Icons.Filled.Palette) { viewModel.openDialog(LoomiDialog.THEMES) },
                        ActionItem("SETTINGS", Icons.Filled.Settings) { viewModel.openDialog(LoomiDialog.SETTINGS) },
                        ActionItem("ABOUT", Icons.Filled.Info) { viewModel.openDialog(LoomiDialog.ABOUT) },
                    )

                    actionButtons.chunked(2).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            rowItems.forEach { action ->
                                RetroActionButton(
                                    label = action.label,
                                    icon = action.icon,
                                    modifier = Modifier.weight(1f),
                                    onClick = action.onClick,
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = viewModel.sessionDisplay(),
                            color = colors.onSurfaceVariant,
                            fontFamily = LoomiFonts.Body,
                            fontSize = 12.sp,
                        )
                        if (uiState.sleepTimerRemainingSeconds != null) {
                            Text(
                                text = "TIMER ${viewModel.sleepTimerDisplay()}",
                                color = colors.primary,
                                fontFamily = LoomiFonts.Body,
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
            }
        }
    }

    when (uiState.activeDialog) {
        LoomiDialog.CHANNELS -> ChannelsDialog(uiState, viewModel)
        LoomiDialog.TIMER -> TimerDialog(uiState, viewModel)
        LoomiDialog.STATS -> StatsDialog(uiState, viewModel)
        LoomiDialog.AMBIENCE -> AmbienceDialog(uiState, viewModel)
        LoomiDialog.MIXER -> MixerDialog(uiState, viewModel)
        LoomiDialog.EQUALIZER -> EqualizerDialog(uiState, viewModel)
        LoomiDialog.THEMES -> ThemesDialog(uiState, viewModel)
        LoomiDialog.SETTINGS -> SettingsDialog(uiState, viewModel)
        LoomiDialog.ABOUT -> AboutDialog(viewModel)
        null -> Unit
    }
}

@Composable
private fun RetroFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .border(2.dp, colors.outline),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(colors.primary, Color(0xFFFBBF24), Color(0xFF4ADE80), colors.primary),
                    ),
                ),
        )
        Box(modifier = Modifier.padding(top = 3.dp)) { content() }
    }
}

@Composable
private fun RetroPanel(content: @Composable () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surfaceVariant)
            .border(2.dp, colors.outline),
    ) {
        content()
    }
}

@Composable
private fun RetroIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    active: Boolean = false,
    compact: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    val size = if (compact) 34.dp else 42.dp
    val bg = if (active) colors.primary.copy(alpha = 0.22f) else colors.surfaceVariant
    val fg = if (active) colors.primary else colors.onSurfaceVariant

    Box(
        modifier = Modifier
            .size(size)
            .background(bg)
            .border(2.dp, if (active) colors.primary else colors.outline)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = fg)
    }
}

@Composable
private fun RetroPlayButton(isPlaying: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .size(58.dp)
            .background(colors.primary)
            .border(3.dp, colors.outline)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
            contentDescription = null,
            tint = colors.onPrimary,
            modifier = Modifier.size(30.dp),
        )
    }
}

@Composable
private fun RetroActionButton(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .background(colors.surfaceVariant)
            .border(2.dp, colors.outline)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = colors.onSurfaceVariant, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = colors.onSurface,
            fontSize = 13.sp,
            fontFamily = LoomiFonts.Body,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

@Composable
private fun RetroTextButton(
    text: String,
    onClick: () -> Unit,
    highlighted: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    val background = if (highlighted) colors.primary else colors.surfaceVariant
    val foreground = if (highlighted) colors.onPrimary else colors.onSurface

    Box(
        modifier = modifier
            .background(background)
            .border(2.dp, if (highlighted) colors.primary else colors.outline)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = foreground,
            fontFamily = LoomiFonts.Body,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
        )
    }
}

@Composable
private fun RetroVolumeBar(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = MaterialTheme.colorScheme
    var barWidthPx by remember { mutableStateOf(1f) }
    val thumbSize = 12.dp
    val clamped = value.coerceIn(0f, 1f)

    val activeColor = if (enabled) colors.primary else colors.outline
    val borderColor = if (enabled) colors.outline else colors.outline.copy(alpha = 0.6f)
    val trackColor = if (enabled) colors.background else colors.surfaceVariant

    Box(
        modifier = modifier
            .height(22.dp)
            .onSizeChanged { barWidthPx = it.width.toFloat().coerceAtLeast(1f) }
            .pointerInput(enabled, barWidthPx) {
                if (!enabled) return@pointerInput
                detectTapGestures { pos ->
                    onValueChange((pos.x / barWidthPx).coerceIn(0f, 1f))
                }
            }
            .pointerInput(enabled, barWidthPx) {
                if (!enabled) return@pointerInput
                detectDragGestures { change, _ ->
                    onValueChange((change.position.x / barWidthPx).coerceIn(0f, 1f))
                }
            }
            .background(trackColor)
            .border(2.dp, borderColor)
            .padding(2.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(clamped)
                .fillMaxHeight()
                .background(activeColor.copy(alpha = 0.85f)),
        )

        val thumbSizePx = with(androidx.compose.ui.platform.LocalDensity.current) { thumbSize.toPx() }
        val maxOffset = (barWidthPx - thumbSizePx).coerceAtLeast(0f)
        val thumbOffsetPx = (maxOffset * clamped).roundToInt()
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset { IntOffset(thumbOffsetPx, 0) }
                .size(thumbSize)
                .background(activeColor)
                .border(2.dp, if (enabled) colors.onPrimary else colors.outline),
        )
    }
}

@Composable
private fun RetroToggle(checked: Boolean, onToggle: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .width(44.dp)
            .height(24.dp)
            .background(if (checked) colors.primary else colors.outline)
            .border(2.dp, if (checked) colors.primary else colors.outline)
            .clickable(onClick = onToggle)
            .padding(horizontal = 2.dp, vertical = 2.dp),
    ) {
        Box(
            modifier = Modifier
                .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart)
                .size(16.dp)
                .background(colors.onPrimary),
        )
    }
}

@Composable
private fun RetroDialogShell(
    title: String,
    onDismiss: () -> Unit,
    contentMaxHeight: Dp = 460.dp,
    headerTrailing: (@Composable RowScope.() -> Unit)? = null,
    footer: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surface)
                .border(3.dp, colors.outline),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surfaceVariant)
                        .border(1.dp, colors.outline)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = title,
                        color = colors.primary,
                        fontFamily = LoomiFonts.Pixel,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        headerTrailing?.invoke(this)
                        Spacer(modifier = Modifier.width(6.dp))
                        RetroIconButton(icon = Icons.Filled.Close, compact = true, onClick = onDismiss)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = contentMaxHeight)
                        .padding(10.dp),
                    content = content,
                )

                if (footer != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        content = footer,
                    )
                }
            }
        }
    }
}

@Composable
private fun ChannelsDialog(uiState: LoomiUiState, viewModel: LoomiViewModel) {
    RetroDialogShell(
        title = "CHANNELS",
        onDismiss = { viewModel.closeDialog() },
        headerTrailing = {
            RetroIconButton(icon = Icons.Filled.Refresh, compact = true, onClick = { viewModel.scanStations() })
        },
        footer = {
            RetroTextButton("CLOSE", onClick = { viewModel.closeDialog() }, modifier = Modifier.weight(1f))
        },
    ) {
        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
            if (uiState.isScanningStations) {
                Text("Scanning stations...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = LoomiFonts.Body)
                return@Column
            }

            if (uiState.stationLoadError != null) {
                Text(
                    text = uiState.stationLoadError,
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = LoomiFonts.Body,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }

            if (uiState.favoriteStations.isNotEmpty()) {
                ChannelSection("FAVORITE CHANNELS", uiState.favoriteStations, uiState, viewModel)
            }
            if (uiState.lofiStations.isNotEmpty()) {
                ChannelSection("LOFI CHANNELS", uiState.lofiStations, uiState, viewModel)
            }
            if (uiState.otherStations.isNotEmpty()) {
                ChannelSection("OTHER CHANNELS", uiState.otherStations, uiState, viewModel)
            }

            if (uiState.favoriteStations.isEmpty() && uiState.lofiStations.isEmpty() && uiState.otherStations.isEmpty()) {
                Text("No stations loaded yet. Press rescan.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = LoomiFonts.Body)
            }
        }
    }
}

@Composable
private fun ChannelSection(
    title: String,
    stations: List<RadioStation>,
    uiState: LoomiUiState,
    viewModel: LoomiViewModel,
) {
    val colors = MaterialTheme.colorScheme
    Text(
        text = title,
        color = colors.onSurfaceVariant,
        fontSize = 12.sp,
        letterSpacing = 1.sp,
        fontFamily = LoomiFonts.Body,
        modifier = Modifier.padding(top = 6.dp, bottom = 4.dp),
    )

    stations.forEach { station ->
        val active = uiState.currentStation?.stationuuid == station.stationuuid
        val isFavorite = viewModel.isFavorite(station)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
                .background(if (active) colors.primary.copy(alpha = 0.16f) else colors.surfaceVariant)
                .border(2.dp, if (active) colors.primary else colors.outline)
                .clickable { viewModel.selectStation(station) }
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = station.name,
                color = colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = LoomiFonts.Body,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f),
            )
            RetroIconButton(
                icon = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                compact = true,
                active = isFavorite,
                onClick = { viewModel.toggleFavorite(station) },
            )
        }
    }
}

@Composable
private fun TimerDialog(uiState: LoomiUiState, viewModel: LoomiViewModel) {
    val fixedOptions = remember { LoomiConfig.timerOptionsMinutes.filter { it != 120 } }
    var customTimerMinutesInput by remember(uiState.selectedTimerMinutes) {
        mutableStateOf(
            uiState.selectedTimerMinutes
                ?.takeIf { it !in fixedOptions }
                ?.toString()
                .orEmpty(),
        )
    }

    RetroDialogShell(
        title = "SLEEP TIMER",
        onDismiss = { viewModel.closeDialog() },
        footer = {
            RetroTextButton("CANCEL", onClick = { viewModel.cancelSleepTimer() }, modifier = Modifier.weight(1f))
            RetroTextButton(
                "SET",
                onClick = {
                    customTimerMinutesInput.toIntOrNull()
                        ?.coerceIn(1, 999)
                        ?.let(viewModel::selectTimerOption)
                    viewModel.startSleepTimer()
                },
                highlighted = true,
                modifier = Modifier.weight(1f),
            )
        },
    ) {
        LoomiConfig.timerOptionsMinutes.chunked(4).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { mins ->
                    if (mins == 120) {
                        RetroTimerInput(
                            value = customTimerMinutesInput,
                            onValueChange = { value ->
                                val filtered = value.filter(Char::isDigit).take(3)
                                customTimerMinutesInput = filtered
                                filtered.toIntOrNull()
                                    ?.coerceIn(1, 999)
                                    ?.let(viewModel::selectTimerOption)
                            },
                            modifier = Modifier.weight(1f),
                            selected = uiState.selectedTimerMinutes?.let { it !in fixedOptions } == true,
                        )
                    } else {
                        val selected = uiState.selectedTimerMinutes == mins
                        RetroTextButton(
                            text = "${mins}m",
                            highlighted = selected,
                            onClick = { viewModel.selectTimerOption(mins) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
private fun RetroTimerInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .background(colors.surfaceVariant)
            .border(2.dp, if (selected) colors.primary else colors.outline)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = colors.onSurface,
                fontFamily = LoomiFonts.Body,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            ),
            cursorBrush = SolidColor(colors.primary),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->
                if (value.isBlank()) {
                    Text(
                        text = "CUSTOM",
                        color = colors.onSurfaceVariant,
                        fontFamily = LoomiFonts.Body,
                        fontSize = 13.sp,
                    )
                }
                innerTextField()
            },
        )
        Text("m", color = colors.primary, fontFamily = LoomiFonts.Body, fontSize = 13.sp)
    }
}

@Composable
private fun StatsDialog(uiState: LoomiUiState, viewModel: LoomiViewModel) {
    RetroDialogShell(
        title = "LISTEN STATS",
        onDismiss = { viewModel.closeDialog() },
        footer = {
            RetroTextButton("CLEAR", onClick = { viewModel.clearListenStats() }, modifier = Modifier.weight(1f))
            RetroTextButton("CLOSE", onClick = { viewModel.closeDialog() }, highlighted = true, modifier = Modifier.weight(1f))
        },
    ) {
        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
            Text(
                text = "TOTAL: ${viewModel.totalListenTimeText()}",
                color = MaterialTheme.colorScheme.primary,
                fontFamily = LoomiFonts.Body,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            val entries = viewModel.formattedStats()
            if (entries.isEmpty()) {
                Text("No listening data yet.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = LoomiFonts.Body)
            } else {
                entries.forEach { (name, value) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 5.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(2.dp, MaterialTheme.colorScheme.outline)
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = name,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = LoomiFonts.Body,
                        )
                        Text(value, color = MaterialTheme.colorScheme.primary, fontFamily = LoomiFonts.Body)
                    }
                }
            }
        }
    }
}

@Composable
private fun AmbienceDialog(uiState: LoomiUiState, viewModel: LoomiViewModel) {
    RetroDialogShell(
        title = "AMBIENCE",
        onDismiss = { viewModel.closeDialog() },
        footer = {
            RetroTextButton("DONE", onClick = { viewModel.closeDialog() }, highlighted = true, modifier = Modifier.weight(1f))
        },
    ) {
        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
            LoomiConfig.ambienceTracks.forEach { track ->
                AmbienceItem(track = track, uiState = uiState, viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun AmbienceItem(track: AmbienceTrack, uiState: LoomiUiState, viewModel: LoomiViewModel) {
    val colors = MaterialTheme.colorScheme
    val state = uiState.ambienceStates[track.id] ?: AmbienceState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .background(if (state.active) colors.primary.copy(alpha = 0.12f) else colors.surfaceVariant)
            .border(2.dp, if (state.active) colors.primary else colors.outline)
            .padding(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(track.name, color = colors.onSurface, fontFamily = LoomiFonts.Body)
            RetroToggle(checked = state.active, onToggle = { viewModel.toggleAmbience(track.id) })
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            RetroVolumeBar(
                value = state.volume / 100f,
                onValueChange = { viewModel.updateAmbienceTrackVolume(track.id, (it * 100).roundToInt()) },
                modifier = Modifier.weight(1f),
                enabled = state.active,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${state.volume}%",
                color = if (state.active) colors.onSurface else colors.onSurfaceVariant,
                fontFamily = LoomiFonts.Body,
            )
        }
    }
}

@Composable
private fun MixerDialog(uiState: LoomiUiState, viewModel: LoomiViewModel) {
    RetroDialogShell(
        title = "VOLUME MIXER",
        onDismiss = { viewModel.closeDialog() },
        footer = {
            RetroTextButton("CLOSE", onClick = { viewModel.closeDialog() }, highlighted = true, modifier = Modifier.weight(1f))
        },
    ) {
        MixerSlider("MASTER", uiState.volume) { viewModel.updateMasterVolume(it) }
        MixerSlider("MUSIC", uiState.musicVolume) { viewModel.updateMusicVolume(it) }
        MixerSlider("AMBIENCE", uiState.ambienceVolume) { viewModel.updateAmbienceVolume(it) }
    }
}

@Composable
private fun MixerSlider(label: String, value: Float, onUpdate: (Int) -> Unit) {
    val colors = MaterialTheme.colorScheme
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .background(colors.surfaceVariant)
                .border(2.dp, colors.outline)
                .padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = colors.onSurfaceVariant, fontFamily = LoomiFonts.Body)
            Text("${(value * 100).toInt()}%", color = colors.onSurface, fontFamily = LoomiFonts.Body)
        }
        RetroVolumeBar(
            value = value,
            onValueChange = { onUpdate((it * 100).roundToInt()) },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun EqualizerDialog(uiState: LoomiUiState, viewModel: LoomiViewModel) {
    val colors = MaterialTheme.colorScheme

    RetroDialogShell(
        title = "EQUALIZER",
        onDismiss = { viewModel.closeDialog() },
        contentMaxHeight = 560.dp,
        footer = {
            RetroTextButton("RESET", onClick = { viewModel.resetEqualizer() }, modifier = Modifier.weight(1f))
            RetroTextButton("DONE", onClick = { viewModel.closeDialog() }, highlighted = true, modifier = Modifier.weight(1f))
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
        ) {
            LoomiConfig.eqPresets.forEach { preset ->
                val active = preset.values == uiState.eqValues
                RetroTextButton(
                    text = preset.name.uppercase(),
                    highlighted = active,
                    onClick = { viewModel.applyEqPreset(preset.id) },
                    modifier = Modifier.padding(end = 6.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 380.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            LoomiConfig.eqBands.indices.forEach { index ->
                val band = LoomiConfig.eqBands[index]
                val value = uiState.eqValues.getOrElse(index) { 0 }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .background(colors.surfaceVariant)
                        .border(2.dp, colors.outline)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${band.label} HZ", color = colors.onSurfaceVariant, fontFamily = LoomiFonts.Body)
                        Text(
                            text = "${if (value > 0) "+" else ""}${value} dB",
                            color = colors.primary,
                            fontFamily = LoomiFonts.Body,
                        )
                    }
                    RetroVolumeBar(
                        value = ((value + 12f) / 24f).coerceIn(0f, 1f),
                        onValueChange = { normalized ->
                            val db = (normalized * 24f - 12f).roundToInt().coerceIn(-12, 12)
                            viewModel.updateEqBand(index, db)
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemesDialog(uiState: LoomiUiState, viewModel: LoomiViewModel) {
    val gifImageLoader = rememberGifImageLoader()
    RetroDialogShell(
        title = "THEMES",
        onDismiss = { viewModel.closeDialog() },
        footer = {
            RetroTextButton("CLOSE", onClick = { viewModel.closeDialog() }, highlighted = true, modifier = Modifier.weight(1f))
        },
    ) {
        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
            LoomiConfig.themes.forEach { theme ->
                val active = uiState.currentThemeId == theme.id
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                        .padding(bottom = 8.dp)
                        .border(3.dp, if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                        .clickable { viewModel.setTheme(theme.id) },
                ) {
                    AsyncImage(
                        model = theme.imageUrl,
                        imageLoader = gifImageLoader,
                        contentDescription = theme.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)))
                    Text(
                        text = if (active) "${theme.name.uppercase()} [ACTIVE]" else theme.name.uppercase(),
                        color = Color.White,
                        fontFamily = LoomiFonts.Body,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsDialog(uiState: LoomiUiState, viewModel: LoomiViewModel) {
    val colors = MaterialTheme.colorScheme
    var nameInput by remember(uiState.userName) { mutableStateOf(uiState.userName) }

    RetroDialogShell(
        title = "SETTINGS",
        onDismiss = { viewModel.closeDialog() },
        footer = {
            RetroTextButton("DONE", onClick = { viewModel.closeDialog() }, highlighted = true, modifier = Modifier.weight(1f))
        },
    ) {
        Text("YOUR NAME", color = colors.onSurfaceVariant, fontFamily = LoomiFonts.Body, modifier = Modifier.padding(bottom = 6.dp))
        BasicTextField(
            value = nameInput,
            onValueChange = {
                nameInput = it.take(12)
                viewModel.updateUserName(nameInput)
            },
            textStyle = TextStyle(
                color = colors.onSurface,
                fontFamily = LoomiFonts.Body,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
            ),
            cursorBrush = SolidColor(colors.primary),
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.background)
                .border(2.dp, colors.outline)
                .padding(horizontal = 10.dp, vertical = 8.dp),
        )
    }
}

@Composable
private fun AboutDialog(viewModel: LoomiViewModel) {
    val colors = MaterialTheme.colorScheme
    val uriHandler = LocalUriHandler.current

    RetroDialogShell(
        title = "ABOUT",
        onDismiss = { viewModel.closeDialog() },
        footer = {
            RetroTextButton("CLOSE", onClick = { viewModel.closeDialog() }, highlighted = true, modifier = Modifier.weight(1f))
        },
    ) {
        Text("LOOMI v1.0", color = colors.primary, fontFamily = LoomiFonts.Pixel, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Your retro lofi companion for chill beats and relaxing sessions.",
            color = colors.onSurface,
            fontFamily = LoomiFonts.Body,
            fontSize = 13.sp,
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text("CREDITS", color = colors.primary, fontFamily = LoomiFonts.Pixel, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("- Stations: Radio Browser API", color = colors.onSurfaceVariant, fontFamily = LoomiFonts.Body, fontSize = 13.sp)
        Text("- Ambience: Pixabay", color = colors.onSurfaceVariant, fontFamily = LoomiFonts.Body, fontSize = 13.sp)
        Text("- GIFs: Tenor", color = colors.onSurfaceVariant, fontFamily = LoomiFonts.Body, fontSize = 13.sp)

        Spacer(modifier = Modifier.height(10.dp))
        RetroTextButton(
            text = "OPEN WEB APP",
            onClick = { uriHandler.openUri("https://loomi-pied.vercel.app/") },
            highlighted = false,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        RetroTextButton(
            text = "CREATOR SITE",
            onClick = { uriHandler.openUri("https://unitreign.github.io/") },
            highlighted = false,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private data class ActionItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
)

@Composable
private fun rememberGifImageLoader(): ImageLoader {
    val context = LocalContext.current
    return remember(context) {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }
}

private fun statusColor(status: String, playingColor: Color, errorColor: Color, idleColor: Color): Color {
    return when (status) {
        "Streaming" -> playingColor
        "Error" -> errorColor
        else -> idleColor
    }
}

@Composable
private fun retroTitleStyle(): TextStyle {
    val colors = MaterialTheme.colorScheme
    return TextStyle(
        color = colors.onBackground,
        fontFamily = LoomiFonts.Pixel,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = 0.4.sp,
    )
}
