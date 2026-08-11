package ru.plumsoftware.radiofm.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.plumsoftware.radiofm.data.FavoritesRepository
import ru.plumsoftware.radiofm.data.RadioStationsRepository
import ru.plumsoftware.radiofm.model.RadioStation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RadioListViewModel(private val favoritesRepository: FavoritesRepository) : ViewModel() {

    private val allStations = RadioStationsRepository.stations

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedFilter = MutableStateFlow<StationFilter>(StationFilter.All)
    val selectedFilter: StateFlow<StationFilter> = _selectedFilter.asStateFlow()

    val favoriteIds: StateFlow<Set<String>> = favoritesRepository.favoriteIds.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptySet(),
    )

    val filteredStations: StateFlow<List<RadioStation>> = combine(
        _query,
        _selectedFilter,
        favoriteIds,
    ) { query, filter, favorites ->
        allStations
            .filter { station -> matchesFilter(station, filter, favorites) }
            .filter { station -> matchesQuery(station, query) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = allStations,
    )

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun onFilterSelected(filter: StationFilter) {
        _selectedFilter.value = filter
    }

    fun toggleFavorite(stationId: String) {
        viewModelScope.launch {
            favoritesRepository.toggleFavorite(stationId)
        }
    }

    private fun matchesFilter(station: RadioStation, filter: StationFilter, favorites: Set<String>): Boolean {
        return when (filter) {
            StationFilter.All -> true
            StationFilter.Favorites -> station.id in favorites
            is StationFilter.ByCategory -> station.category == filter.category
        }
    }

    private fun matchesQuery(station: RadioStation, query: String): Boolean {
        if (query.isBlank()) return true
        return station.name.contains(query, ignoreCase = true) ||
            station.genre.contains(query, ignoreCase = true)
    }
}
