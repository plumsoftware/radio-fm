package ru.plumsoftware.radiofm.ui.screens.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SettingsApplications
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.plumsoftware.radiofm.SettingsViewModelFactory
import ru.plumsoftware.radiofm.ui.theme.AppThemeMode
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themeMode: AppThemeMode,
    onSetThemeMode: (AppThemeMode) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(context.applicationContext),
    )
    val permissionsState by viewModel.state.collectAsState()

    // Уведомления/батарея/фон меняются в системных настройках, а не в нашем приложении —
    // Android об этом никак не уведомляет. Перечитываем статус при каждом возврате экрана
    // на передний план (в т.ч. когда пользователь вернулся из системных настроек назад).
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Настройки",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            SettingsSectionTitle("Тема")

            ThemeOptionRow(
                label = "Как в системе",
                icon = Icons.Default.SettingsBrightness,
                selected = themeMode == AppThemeMode.SYSTEM,
                onClick = { onSetThemeMode(AppThemeMode.SYSTEM) },
            )
            ThemeOptionRow(
                label = "Светлая",
                icon = Icons.Default.LightMode,
                selected = themeMode == AppThemeMode.LIGHT,
                onClick = { onSetThemeMode(AppThemeMode.LIGHT) },
            )
            ThemeOptionRow(
                label = "Тёмная",
                icon = Icons.Default.DarkMode,
                selected = themeMode == AppThemeMode.DARK,
                onClick = { onSetThemeMode(AppThemeMode.DARK) },
            )

            SettingsSectionTitle("Разрешения")

            PermissionRow(
                icon = Icons.Default.Notifications,
                title = "Уведомления",
                subtitle = if (permissionsState.notificationsEnabled) {
                    "Разрешены"
                } else {
                    "Запрещены — уведомление о радио показываться не будет"
                },
                onClick = { context.openNotificationSettings() },
            )
            PermissionRow(
                icon = Icons.Default.Storage,
                title = "Память",
                subtitle = "Открыть в настройках приложения",
                onClick = { context.openAppInfoSettings() },
            )
            PermissionRow(
                icon = Icons.Default.SettingsApplications,
                title = "Ограничения в фоне",
                subtitle = if (permissionsState.backgroundActivityAllowed) {
                    "Не ограничено"
                } else {
                    "Система ограничивает работу в фоне — радио может останавливаться"
                },
                onClick = { context.openAppInfoSettings() },
            )
            PermissionRow(
                icon = Icons.Default.Autorenew,
                title = "Автозапуск / работа в фоне",
                subtitle = "На некоторых телефонах (Xiaomi, Huawei и т.п.) без этого радио " +
                        "останавливается при закрытии приложения",
                onClick = { context.openAutostartSettings() },
            )
            PermissionRow(
                icon = Icons.Default.BatteryAlert,
                title = "Ограничения батарейки",
                subtitle = if (permissionsState.batteryUnrestricted) {
                    "Без ограничений"
                } else {
                    "Система может останавливать радио для экономии батареи"
                },
                onClick = { context.requestIgnoreBatteryOptimizations() },
            )
        }
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 4.dp),
    )
}

@Composable
private fun ThemeOptionRow(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
        )
        RadioButton(selected = selected, onClick = onClick)
    }
}

@Composable
private fun PermissionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun Context.openNotificationSettings() {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
    }
    startActivity(intent)
}

private fun Context.openAppInfoSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }
    startActivity(intent)
}

/**
 * Официального API для "автозапуска" не существует — это проприетарная фича конкретных
 * производителей, у каждого свой экран. Пробуем известные варианты по очереди; если ни
 * один не нашёлся (другой производитель или прошивка поменяла путь) — открываем общий
 * экран информации о приложении.
 */
private fun Context.openAutostartSettings() {
    val manufacturer = Build.MANUFACTURER.lowercase()
    val candidates = when {
        manufacturer.contains("xiaomi") -> listOf(
            "com.miui.securitycenter" to "com.miui.permcenter.autostart.AutoStartManagementActivity",
        )
        manufacturer.contains("huawei") || manufacturer.contains("honor") -> listOf(
            "com.huawei.systemmanager" to "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity",
            "com.huawei.systemmanager" to "com.huawei.systemmanager.optimize.process.ProtectActivity",
        )
        manufacturer.contains("oppo") -> listOf(
            "com.coloros.safecenter" to "com.coloros.safecenter.permission.startup.StartupAppListActivity",
        )
        manufacturer.contains("vivo") -> listOf(
            "com.vivo.permissionmanager" to "com.vivo.permissionmanager.activity.BgStartUpManagerActivity",
        )
        manufacturer.contains("oneplus") -> listOf(
            "com.oneplus.security" to "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity",
        )
        else -> emptyList()
    }

    val found = candidates.any { (pkg, cls) ->
        try {
            startActivity(Intent().setClassName(pkg, cls))
            true
        } catch (e: Exception) {
            false
        }
    }
    if (!found) openAppInfoSettings()
}

private fun Context.requestIgnoreBatteryOptimizations() {
    try {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:$packageName")
        }
        startActivity(intent)
    } catch (e: Exception) {
        openAppInfoSettings()
    }
}