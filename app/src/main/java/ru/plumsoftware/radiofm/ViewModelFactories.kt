package ru.plumsoftware.radiofm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import ru.plumsoftware.radiofm.data.FavoritesRepository
import ru.plumsoftware.radiofm.data.ThemePreferences
import ru.plumsoftware.radiofm.player.RadioPlayerManager
import ru.plumsoftware.radiofm.ui.screens.list.RadioListViewModel
import ru.plumsoftware.radiofm.ui.screens.player.PlayerViewModel
import ru.plumsoftware.radiofm.ui.screens.settings.SettingsViewModel

class MainViewModelFactory(private val themePreferences: ThemePreferences) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return MainViewModel(themePreferences) as T
    }
}

class RadioListViewModelFactory(
    private val favoritesRepository: FavoritesRepository,
    private val radioPlayerManager: RadioPlayerManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return RadioListViewModel(favoritesRepository, radioPlayerManager) as T
    }
}

class PlayerViewModelFactory(
    private val playerManager: RadioPlayerManager,
    private val favoritesRepository: FavoritesRepository,
    private val stationId: String,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return PlayerViewModel(playerManager, favoritesRepository, stationId) as T
    }
}

class SettingsViewModelFactory(private val appContext: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return SettingsViewModel(appContext) as T
    }
}
