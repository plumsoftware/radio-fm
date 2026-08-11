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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    stationId: String,
    radioPlayerManager: RadioPlayerManager,
    favoritesRepository: FavoritesRepository,
    onBack: () -> Unit,
) {
    // stationId меняется при переключении на соседнюю станцию (next/prev),
    // ViewModel пересоздаётся для нового id.
    var currentStationId by remember(stationId) { mutableStateOf(stationId) }

    val viewModel: PlayerViewModel = viewModel(
        key = currentStationId,
        factory = PlayerViewModelFactory(radioPlayerManager, favoritesRepository, currentStationId),
    )
    val station = viewModel.station
    val playbackState by viewModel.playbackState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

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
                    IconButton(onClick = { /* история прослушивания */ }) {
                        Icon(Icons.Default.History, contentDescription = "История")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        // Sticky-баннер закреплён внизу экрана и не перекрывает основной контент плеера.
        bottomBar = { StickyBannerAd() },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(MaterialTheme.shapes.large)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.background,
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = when (playbackState.status) {
                            PlaybackStatus.BUFFERING -> "Буферизация…"
                            PlaybackStatus.PLAYING -> playbackState.nowPlayingTitle ?: (station?.genre ?: "")
                            PlaybackStatus.ERROR -> "Не удалось загрузить поток"
                            else -> station?.genre.orEmpty()
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                            imageVector = if (playbackState.status == PlaybackStatus.PLAYING) {
                                Icons.Default.Pause
                            } else {
                                Icons.Default.PlayArrow
                            },
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

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
