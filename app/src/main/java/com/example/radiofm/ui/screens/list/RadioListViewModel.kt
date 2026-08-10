package com.example.radiofm.ui.screens.list

import androidx.lifecycle.ViewModel
import com.example.radiofm.data.RadioStationsRepository
import com.example.radiofm.model.RadioStation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RadioListViewModel : ViewModel() {

    private val allStations = RadioStationsRepository.stations

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _filtered = MutableStateFlow(allStations)
    val filteredStations: StateFlow<List<RadioStation>> = _filtered.asStateFlow()

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        _filtered.value = if (newQuery.isBlank()) {
            allStations
        } else {
            allStations.filter { station ->
                station.name.contains(newQuery, ignoreCase = true) ||
                    station.genre.contains(newQuery, ignoreCase = true)
            }
        }
    }
}
