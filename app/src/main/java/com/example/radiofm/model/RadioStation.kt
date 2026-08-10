package com.example.radiofm.model

import androidx.compose.ui.graphics.Color

/**
 * Модель радиостанции.
 *
 * [streamUrl] — прямой адрес аудиопотока (icecast/shoutcast/mp3/aac).
 * [accentColor] используется для генерируемой "обложки" станции (кружок с инициалом),
 * т.к. в проект не встроены сторонние логотипы радиостанций.
 */
data class RadioStation(
    val id: String,
    val name: String,
    val genre: String,
    val streamUrl: String,
    val accentColor: Color,
)
