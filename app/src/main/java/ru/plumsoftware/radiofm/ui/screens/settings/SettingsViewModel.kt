package ru.plumsoftware.radiofm.ui.screens.settings

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PermissionsUiState(
    val notificationsEnabled: Boolean = false,
    val batteryUnrestricted: Boolean = false,
    val backgroundActivityAllowed: Boolean = true,
)

/**
 * Статусы уведомлений/батареи/фона — это состояния ОС, а не нашего приложения, и Android
 * не шлёт никаких событий, когда пользователь меняет их в системных настройках. Поэтому
 * это не реактивный Flow, а просто снимок на момент вызова [refresh] — экран сам
 * перечитывает его при каждом возврате на передний план (см. SettingsScreen).
 */
class SettingsViewModel(private val appContext: Context) : ViewModel() {

    private val _state = MutableStateFlow(PermissionsUiState())
    val state: StateFlow<PermissionsUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        val powerManager = appContext.getSystemService(Context.POWER_SERVICE) as? PowerManager
        val batteryUnrestricted = powerManager
            ?.isIgnoringBatteryOptimizations(appContext.packageName)
            ?: false

        val backgroundActivityAllowed = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val activityManager = appContext.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            activityManager?.isBackgroundRestricted?.not() ?: true
        } else {
            true
        }

        _state.value = PermissionsUiState(
            notificationsEnabled = NotificationManagerCompat.from(appContext).areNotificationsEnabled(),
            batteryUnrestricted = batteryUnrestricted,
            backgroundActivityAllowed = backgroundActivityAllowed,
        )
    }
}