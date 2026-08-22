package ru.plumsoftware.radiofm.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.plumsoftware.radiofm.PlayerViewModelFactory
import ru.plumsoftware.radiofm.data.FavoritesRepository
import ru.plumsoftware.radiofm.player.PlaybackStatus
import ru.plumsoftware.radiofm.player.RadioPlayerManager
import ru.plumsoftware.radiofm.ui.components.StationAvatar
import ru.plumsoftware.radiofm.ui.components.StickyBannerAd
import androidx.compose.material3.Snackbar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.isActive
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    stationId: String,
    radioPlayerManager: RadioPlayerManager,
    favoritesRepository: FavoritesRepository,
    onBack: () -> Unit,
) {
    var currentStationId by remember(stationId) { mutableStateOf(stationId) }

    val viewModel: PlayerViewModel = viewModel(
        key = currentStationId,
        factory = PlayerViewModelFactory(radioPlayerManager, favoritesRepository, currentStationId),
    )
    val station = viewModel.station
    val playbackState by viewModel.playbackState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val isPlaying = playbackState.status == PlaybackStatus.PLAYING

    // Плавное вращение: пока играет — крутится, на паузе останавливается на текущем
    // угле (не прыгает в 0) и продолжает с него же при возобновлении. 45°/с — один
    // оборот за 8 секунд, неспеша.
    var discAngle by remember { mutableStateOf(0f) }
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            var lastFrameNanos = withFrameNanos { it }
            while (isActive) {
                val frameNanos = withFrameNanos { it }
                val deltaSeconds = (frameNanos - lastFrameNanos) / 1_000_000_000f
                lastFrameNanos = frameNanos
                discAngle = (discAngle + deltaSeconds * 45f) % 360f
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = station?.name.orEmpty(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "В избранное",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Диск и кнопки теперь сразу под шапкой — вверху, а не после огромного
            // пустого блока (раньше туда попадал градиентный Box со статус-текстом).
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth(),
            ) {
                IconButton(
                    onClick = {
                        viewModel.playAdjacentStation(forward = false)?.let { currentStationId = it.id }
                    },
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Предыдущая станция")
                }

                Box(contentAlignment = Alignment.Center) {
                    if (station != null) {
                        StationAvatar(
                            name = station.name,
                            color = station.accentColor,
                            size = 180.dp,
                            shape = CircleShape,
                            iconRes = station.iconRes,
                            modifier = Modifier.graphicsLayer { rotationZ = discAngle },
                        )
                    }

                    if (playbackState.status == PlaybackStatus.BUFFERING) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(196.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp,
                        )
                    }

                    FloatingActionButton(
                        onClick = { viewModel.togglePlayPause() },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 4.dp, end = 4.dp)
                            .size(56.dp),
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                        )
                    }
                }

                IconButton(
                    onClick = {
                        viewModel.playAdjacentStation(forward = true)?.let { currentStationId = it.id }
                    },
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Следующая станция")
                }
            }

            // Всё, что не влезло сверху, уходит сюда — диск с кнопками остаётся
            // в верхней половине экрана, а снекбар с баннером прижаты книзу.
            Spacer(modifier = Modifier.height(16.dp))

            // Название станции + её теги под диском — это то, что раньше жило в
            // градиентном Box'е и пропало вместе с ним.
            Text(
                text = station?.name.orEmpty(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                station?.genre
                    ?.split(",")
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() }
                    ?.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            )
                        }
                    }
            }

            Spacer(modifier = Modifier.weight(1f))

            AnimatedVisibility(
                visible = playbackState.status == PlaybackStatus.BUFFERING ||
                        playbackState.status == PlaybackStatus.ERROR,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Column {
                    Snackbar(
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ) {
                        Text(
                            text = if (playbackState.status == PlaybackStatus.BUFFERING) {
                                "Загрузка…"
                            } else {
                                "Не удалось загрузить поток"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            StickyBannerAd()

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
