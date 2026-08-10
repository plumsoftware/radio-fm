package com.example.radiofm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.radiofm.data.ThemePreferences
import com.example.radiofm.player.RadioPlayerManager
import com.example.radiofm.ui.screens.player.PlayerViewModel

class MainViewModelFactory(private val themePreferences: ThemePreferences) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return MainViewModel(themePreferences) as T
    }
}

class PlayerViewModelFactory(
    private val playerManager: RadioPlayerManager,
    private val stationId: String,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return PlayerViewModel(playerManager, stationId) as T
    }
}
