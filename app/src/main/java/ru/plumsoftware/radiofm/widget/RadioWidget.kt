package ru.plumsoftware.radiofm.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import ru.plumsoftware.radiofm.MainActivity
import ru.plumsoftware.radiofm.data.RadioStationsRepository
import ru.plumsoftware.radiofm.player.PlaybackStateHolder
import ru.plumsoftware.radiofm.player.PlaybackStatus
import androidx.glance.unit.ColorProvider
import ru.plumsoftware.radiofm.R

/**
 * Виджет на рабочем столе. Ничего не хранит сам — при каждой перерисовке читает текущее
 * значение [PlaybackStateHolder.state]. Перерисовку инициирует [WidgetUpdater] всякий раз,
 * когда сервис меняет состояние, поэтому виджет всегда синхронен с уведомлением и экраном
 * плеера в приложении.
 */
class RadioWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetContent()
        }
    }

    @Composable
    private fun WidgetContent() {
        val state = PlaybackStateHolder.state.value
        val station = state.stationId?.let { RadioStationsRepository.findById(it) }
        val isPlayingOrBuffering =
            state.status == PlaybackStatus.PLAYING || state.status == PlaybackStatus.BUFFERING

        val subtitle = when {
            station == null -> "Нажмите, чтобы выбрать станцию"
            state.status == PlaybackStatus.BUFFERING -> "Буферизация…"
            state.status == PlaybackStatus.PAUSED -> "На паузе"
            state.status == PlaybackStatus.ERROR -> "Ошибка потока"
            else -> state.nowPlayingTitle ?: station.genre
        }

        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFF1B1B1F))
                .padding(12.dp)
                .clickable(actionStartActivity<MainActivity>()),
            verticalAlignment = Alignment.Vertical.CenterVertically,
        ) {
            Column(modifier = GlanceModifier.fillMaxWidth().padding(end = 8.dp)) {
                Text(
                    text = station?.name ?: stringResource(R.string.app_name),
                    style = TextStyle(
                        color = ColorProvider(Color.White),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 1,
                )
                Text(
                    text = subtitle,
                    style = TextStyle(color = ColorProvider(Color(0xFFB0B0B8)), fontSize = 12.sp),
                    maxLines = 1,
                )
            }

            Text(
                text = if (isPlayingOrBuffering) "⏸" else "▶",
                style = TextStyle(color = ColorProvider(Color.White), fontSize = 22.sp),
                modifier = GlanceModifier
                    .padding(8.dp)
                    .clickable(actionRunCallback<TogglePlaybackAction>()),
            )
        }
    }
}
