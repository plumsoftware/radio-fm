package ru.plumsoftware.radiofm

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.plumsoftware.radiofm.data.FavoritesRepository
import ru.plumsoftware.radiofm.data.ThemePreferences
import ru.plumsoftware.radiofm.navigation.RadioFmNavGraph
import ru.plumsoftware.radiofm.ui.theme.RadioFmTheme

class MainActivity : ComponentActivity() {

    // Результат не критичен для работы приложения: без разрешения сервис продолжит играть
    // радио в фоне, просто система не покажет само уведомление (актуально для Android 13+).
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestNotificationPermissionIfNeeded()

        val app = application as RadioFmApplication
        val themePreferences = ThemePreferences(applicationContext)
        val favoritesRepository = FavoritesRepository(applicationContext)

        setContent {
            val mainViewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(themePreferences),
            )
            val themeMode by mainViewModel.themeMode.collectAsState()

            RadioFmTheme(themeMode = themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RadioFmNavGraph(
                        radioPlayerManager = app.radioPlayerManager,
                        favoritesRepository = favoritesRepository,
                        themeMode = themeMode,
                        onToggleTheme = mainViewModel::toggleThemeMode,
                        onSetThemeMode = mainViewModel::setThemeMode,
                    )
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
