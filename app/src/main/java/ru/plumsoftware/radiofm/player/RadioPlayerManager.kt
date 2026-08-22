package ru.plumsoftware.radiofm.player

import android.content.Context
import androidx.core.content.ContextCompat
import ru.plumsoftware.radiofm.model.RadioStation
import kotlinx.coroutines.flow.StateFlow

/**
 * Фасад над [RadioPlaybackService], который используют экраны приложения и виджет.
 *
 * Публичный API намеренно не изменился (play/togglePlayPause/stop/state/release), чтобы
 * существующие ViewModel'и (RadioListViewModel, PlayerViewModel) не пришлось трогать.
 * Изменилась только реализация: раньше здесь напрямую жил ExoPlayer, теперь все команды
 * лишь пересылаются в foreground-сервис [RadioPlaybackService] — только он может надёжно
 * играть поток в фоне и показывать несворачиваемое уведомление. Состояние ([state]) — это
 * тот же самый StateFlow, который публикует сервис через [PlaybackStateHolder], поэтому
 * Activity/ViewModel и виджет на рабочем столе всегда видят одинаковые данные.
 */
class RadioPlayerManager(context: Context) {

    private val appContext = context.applicationContext

    val state: StateFlow<PlaybackState> = PlaybackStateHolder.state

    fun play(station: RadioStation) {
        ContextCompat.startForegroundService(appContext, RadioPlaybackService.playIntent(appContext, station.id))
    }

    fun togglePlayPause(station: RadioStation) {
        if (state.value.stationId != station.id) {
            play(station)
            return
        }
        ContextCompat.startForegroundService(appContext, RadioPlaybackService.toggleIntent(appContext))
    }

    fun stop() {
        if (state.value.status == PlaybackStatus.IDLE) return
        appContext.startService(RadioPlaybackService.stopIntent(appContext))
    }

    /**
     * Оставлен для совместимости со старыми вызовами (например, из `onCleared()` во
     * ViewModel экрана плеера). Раньше он реально освобождал ExoPlayer при уходе с экрана —
     * из-за этого радио замолкало при простом сворачивании плеера. Теперь жизненным циклом
     * плеера управляет исключительно сервис, поэтому здесь намеренно ничего не делается:
     * воспроизведение и уведомление должны продолжаться, пока пользователь явно не нажмёт
     * "стоп" (см. [stop]).
     */
    fun release() = Unit
}
