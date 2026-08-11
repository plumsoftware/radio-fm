package ru.plumsoftware.radiofm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import ru.plumsoftware.radiofm.data.FavoritesRepository
import ru.plumsoftware.radiofm.data.ThemePreferences
import ru.plumsoftware.radiofm.player.RadioPlayerManager
import ru.plumsoftware.radiofm.ui.screens.list.RadioListViewModel
import ru.plumsoftware.radiofm.ui.screens.player.PlayerViewModel

class MainViewModelFactory(private val themePreferences: ThemePreferences) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return MainViewModel(themePreferences) as T
    }
}

class RadioListViewModelFactory(private val favoritesRepository: FavoritesRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return RadioListViewModel(favoritesRepository) as T
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
