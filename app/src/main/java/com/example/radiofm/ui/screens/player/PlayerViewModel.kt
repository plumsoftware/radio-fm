package com.example.radiofm.ui.screens.player

import androidx.lifecycle.ViewModel
import com.example.radiofm.data.RadioStationsRepository
import com.example.radiofm.model.RadioStation
import com.example.radiofm.player.PlaybackState
import com.example.radiofm.player.RadioPlayerManager
import kotlinx.coroutines.flow.StateFlow

class PlayerViewModel(
    private val playerManager: RadioPlayerManager,
    private val stationId: String,
) : ViewModel() {

    val station: RadioStation? = RadioStationsRepository.findById(stationId)

    val playbackState: StateFlow<PlaybackState> = playerManager.state

    init {
        station?.let(playerManager::play)
    }

    fun togglePlayPause() {
        station?.let(playerManager::togglePlayPause)
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
