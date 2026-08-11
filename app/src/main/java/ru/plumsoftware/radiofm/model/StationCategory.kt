package ru.plumsoftware.radiofm.model

/**
 * Категория станции — используется для чипов-фильтров на главном экране.
 * "Все" и "Избранное" — не категории станции, а отдельные виртуальные режимы фильтра
 * на уровне экрана (см. [ru.plumsoftware.radiofm.ui.screens.list.StationFilter]).
 */
enum class StationCategory(val displayName: String) {
    NEWS("Новости"),
    SPORT("Спорт"),
    POP("Поп"),
    ROCK("Рок"),
    RETRO("Ретро"),
    DANCE("Танцевальная"),
    HUMOR("Юмор"),
    CHANSON("Шансон"),
    TALK("Разговорное"),
}
