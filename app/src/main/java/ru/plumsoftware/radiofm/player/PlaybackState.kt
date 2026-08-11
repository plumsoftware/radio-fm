package ru.plumsoftware.radiofm.player

/**
 * Состояние воспроизведения текущего радиопотока.
 */
enum class PlaybackStatus { IDLE, BUFFERING, PLAYING, PAUSED, ERROR }

data class PlaybackState(
    val stationId: String? = null,
    val status: PlaybackStatus = PlaybackStatus.IDLE,
    val nowPlayingTitle: String? = null,
)
