package ru.plumsoftware.radiofm.ui.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.plumsoftware.radiofm.data.FavoritesRepository
import ru.plumsoftware.radiofm.data.RadioStationsRepository
import ru.plumsoftware.radiofm.model.RadioStation
import ru.plumsoftware.radiofm.player.PlaybackState
import ru.plumsoftware.radiofm.player.RadioPlayerManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerManager: RadioPlayerManager,
    private val favoritesRepository: FavoritesRepository,
    private val stationId: String,
) : ViewModel() {

    val station: RadioStation? = RadioStationsRepository.findById(stationId)

    val playbackState: StateFlow<PlaybackState> = playerManager.state

    val isFavorite: StateFlow<Boolean> = favoritesRepository.favoriteIds
        .map { it.contains(stationId) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    init {
        station?.let(playerManager::play)
    }

    fun togglePlayPause() {
        station?.let(playerManager::togglePlayPause)
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            favoritesRepository.toggleFavorite(stationId)
        }
    }

    /** Переключение на следующую/предыдущую станцию из общего списка. */
    fun playAdjacentStation(forward: Boolean): RadioStation? {
        val all = RadioStationsRepository.stations
        val currentIndex = all.indexOfFirst { it.id == stationId }
        if (currentIndex == -1) return null
        val nextIndex = if (forward) (currentIndex + 1) % all.size else (currentIndex - 1 + all.size) % all.size
        val next = all[nextIndex]
        playerManager.play(next)
        return next
    }
}
