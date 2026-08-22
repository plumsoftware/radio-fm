package ru.plumsoftware.radiofm.player

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Единственный источник истины о состоянии воспроизведения на весь процесс приложения.
 *
 * Раньше [PlaybackState] жил только внутри `RadioPlayerManager`, который сам держал
 * ExoPlayer. Теперь ExoPlayer живёт в [RadioPlaybackService] (foreground-сервис — это
 * необходимо, чтобы поток не обрывался в фоне и чтобы можно было показать несворачиваемое
 * уведомление). Чтобы экраны приложения, сервис и виджет на рабочем столе видели ровно
 * одно и то же состояние, все они читают/пишут через этот объект.
 */
object PlaybackStateHolder {

    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state.asStateFlow()

    fun set(newState: PlaybackState) {
        _state.value = newState
    }

    fun update(transform: (PlaybackState) -> PlaybackState) {
        _state.value = transform(_state.value)
    }
}
