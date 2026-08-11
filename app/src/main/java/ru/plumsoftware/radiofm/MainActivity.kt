package ru.plumsoftware.radiofm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.plumsoftware.radiofm.data.FavoritesRepository
import ru.plumsoftware.radiofm.data.ThemePreferences
import ru.plumsoftware.radiofm.navigation.RadioFmNavGraph
import ru.plumsoftware.radiofm.ui.theme.RadioFmTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
                    )
                }
            }
        }
    }
}
