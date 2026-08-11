package ru.plumsoftware.radiofm.ui.screens.list

import ru.plumsoftware.radiofm.model.StationCategory

/**
 * Выбранный фильтр на главном экране. "Все" и "Избранное" — не категории станции,
 * а отдельные режимы отображения списка.
 */
sealed class StationFilter {
    data object All : StationFilter()
    data object Favorites : StationFilter()
    data class ByCategory(val category: StationCategory) : StationFilter()

    val label: String
        get() = when (this) {
            All -> "Все"
            Favorites -> "Избранное"
            is ByCategory -> category.displayName
        }

    companion object {
        /** Порядок чипов на экране: Все, Избранное, затем все категории. */
        val chips: List<StationFilter> by lazy {
            listOf(All, Favorites) + StationCategory.entries.map(::ByCategory)
        }
    }
}
