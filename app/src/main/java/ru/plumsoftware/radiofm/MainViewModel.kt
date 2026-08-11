package ru.plumsoftware.radiofm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.plumsoftware.radiofm.data.ThemePreferences
import ru.plumsoftware.radiofm.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val themePreferences: ThemePreferences) : ViewModel() {

    val themeMode: StateFlow<AppThemeMode> = themePreferences.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AppThemeMode.SYSTEM,
    )

    fun setThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            themePreferences.setThemeMode(mode)
        }
    }

    /** Циклически переключает: System -> Light -> Dark -> System -> ... */
    fun toggleThemeMode() {
        val next = when (themeMode.value) {
            AppThemeMode.SYSTEM -> AppThemeMode.LIGHT
            AppThemeMode.LIGHT -> AppThemeMode.DARK
            AppThemeMode.DARK -> AppThemeMode.SYSTEM
        }
        setThemeMode(next)
    }
}
