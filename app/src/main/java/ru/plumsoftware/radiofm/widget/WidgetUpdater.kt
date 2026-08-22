package ru.plumsoftware.radiofm.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Glance-виджеты обновляются через suspend-функцию [androidx.glance.appwidget.updateAll], а
 * состояние меняется из [ru.plumsoftware.radiofm.player.RadioPlaybackService], у которого нет
 * своего корутин-скоупа "на экран". Этот объект — единственная точка, откуда сервис и
 * приложение просят Glance перерисовать все размещённые на рабочем столе виджеты, читая
 * актуальные данные из PlaybackStateHolder.
 */
object WidgetUpdater {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun updateAll(context: Context) {
        scope.launch {
            RadioWidget().updateAll(context.applicationContext)
        }
    }
}
