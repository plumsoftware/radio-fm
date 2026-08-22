package ru.plumsoftware.radiofm.widget

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import ru.plumsoftware.radiofm.data.RadioStationsRepository
import ru.plumsoftware.radiofm.player.PlaybackStateHolder
import ru.plumsoftware.radiofm.player.RadioPlaybackService

/**
 * Тап по кнопке play/pause в виджете. Если что-то уже играет/на паузе — переключаем его,
 * иначе (виджет добавлен, но пользователь ещё ничего не запускал) запускаем первую станцию
 * из списка — так у кнопки в виджете всегда есть осмысленное действие.
 */
class TogglePlaybackAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        val state = PlaybackStateHolder.state.value
        val currentStation = state.stationId?.let { RadioStationsRepository.findById(it) }

        val intent = if (currentStation != null) {
            RadioPlaybackService.toggleIntent(context)
        } else {
            val firstStation = RadioStationsRepository.stations.firstOrNull() ?: return
            RadioPlaybackService.playIntent(context, firstStation.id)
        }
        ContextCompat.startForegroundService(context, intent)
    }
}
