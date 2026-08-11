package ru.plumsoftware.radiofm.player

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import ru.plumsoftware.radiofm.model.RadioStation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Единственный на всё приложение менеджер воспроизведения интернет-радио на базе Media3/ExoPlayer.
 *
 * Живёт на уровне Application, поэтому воспроизведение не прерывается при повороте экрана
 * или переходе между экранами списка/плеера.
 */
class RadioPlayerManager(context: Context) {

    private val appContext = context.applicationContext

    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state.asStateFlow()

    private var player: ExoPlayer? = null

    private fun ensurePlayer(): ExoPlayer {
        var p = player
        if (p == null) {
            p = ExoPlayer.Builder(appContext).build()
            p.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    updateStatusFromPlayer()
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    updateStatusFromPlayer()
                }

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    val title = mediaMetadata.title?.toString() ?: mediaMetadata.artist?.toString()
                    if (title != null) {
                        _state.value = _state.value.copy(nowPlayingTitle = title)
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    _state.value = _state.value.copy(status = PlaybackStatus.ERROR)
                }
            })
            player = p
        }
        return p
    }

    private fun updateStatusFromPlayer() {
        val p = player ?: return
        val status = when {
            p.playbackState == Player.STATE_BUFFERING -> PlaybackStatus.BUFFERING
            p.isPlaying -> PlaybackStatus.PLAYING
            p.playbackState == Player.STATE_READY && !p.isPlaying -> PlaybackStatus.PAUSED
            p.playbackState == Player.STATE_IDLE -> _state.value.status
            else -> _state.value.status
        }
        _state.value = _state.value.copy(status = status)
    }

    fun play(station: RadioStation) {
        val p = ensurePlayer()
        if (_state.value.stationId != station.id) {
            _state.value = PlaybackState(stationId = station.id, status = PlaybackStatus.BUFFERING)
            p.setMediaItem(MediaItem.fromUri(station.streamUrl))
            p.prepare()
        }
        p.playWhenReady = true
    }

    fun togglePlayPause(station: RadioStation) {
        val p = player
        if (p == null || _state.value.stationId != station.id) {
            play(station)
            return
        }
        if (p.isPlaying) {
            p.playWhenReady = false
        } else {
            p.playWhenReady = true
        }
    }

    fun stop() {
        player?.stop()
        _state.value = _state.value.copy(status = PlaybackStatus.IDLE)
    }

    fun release() {
        player?.release()
        player = null
    }
}
