package ru.plumsoftware.radiofm.ui.components
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.plumsoftware.radiofm.model.RadioStation
import ru.plumsoftware.radiofm.player.PlaybackState
import ru.plumsoftware.radiofm.player.PlaybackStatus

@Composable
fun MiniPlayerBar(
    station: RadioStation,
    playbackState: PlaybackState,
    onClick: () -> Unit,
    onTogglePlayPause: () -> Unit,
) {
    val isPlayingOrBuffering = playbackState.status == PlaybackStatus.PLAYING ||
            playbackState.status == PlaybackStatus.BUFFERING

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StationAvatar(
            name = station.name,
            color = station.accentColor,
            size = 44.dp,
            shape = RoundedCornerShape(12.dp),
            iconRes = station.iconRes,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        ) {
            Text(
                text = station.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = when (playbackState.status) {
                    PlaybackStatus.BUFFERING -> "Загрузка…"
                    PlaybackStatus.PLAYING -> playbackState.nowPlayingTitle ?: "Сейчас в эфире"
                    PlaybackStatus.PAUSED -> "На паузе"
                    PlaybackStatus.ERROR -> "Ошибка воспроизведения"
                    PlaybackStatus.IDLE -> ""
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
        IconButton(onClick = onTogglePlayPause) {
            Icon(
                imageVector = if (isPlayingOrBuffering) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlayingOrBuffering) "Пауза" else "Играть",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}