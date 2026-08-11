package ru.plumsoftware.radiofm.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

/**
 * Модель радиостанции.
 *
 * [streamUrl] — прямой адрес аудиопотока (icecast/shoutcast/mp3/aac).
 * [category] — используется для чипов-фильтров на главном экране.
 * [iconRes] — ресурс иконки/логотипа станции (drawable). По умолчанию `null` — пока своих
 *   логотипов нет, вместо иконки рисуется генерируемая цветная "обложка" ([accentColor] +
 *   первая буква названия, см. [ru.plumsoftware.radiofm.ui.components.StationAvatar]).
 *   Заполните это поле у нужных станций, когда добавите свои иконки в res/drawable.
 * [accentColor] — акцентный цвет генерируемой обложки и запасной вариант, если [iconRes] не задан.
 */
data class RadioStation(
    val id: String,
    val name: String,
    val genre: String,
    val category: StationCategory,
    val streamUrl: String,
    val accentColor: Color,
    @field:DrawableRes val iconRes: Int? = null,
)
