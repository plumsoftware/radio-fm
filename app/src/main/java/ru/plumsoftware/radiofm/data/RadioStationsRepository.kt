package ru.plumsoftware.radiofm.data

import ru.plumsoftware.radiofm.model.RadioStation
import ru.plumsoftware.radiofm.model.StationCategory
import ru.plumsoftware.radiofm.ui.theme.StationPalette

/**
 * Простой in-memory источник станций — только реальные российские радиостанции.
 *
 * ВАЖНО про потоки: адреса вещания ([RadioStation.streamUrl]) — публичные ретрансляции,
 * которые радиостанции время от времени меняют/переносят на другой CDN. Перед публикацией
 * приложения проверьте каждую ссылку вживую и при необходимости замените на актуальную.
 *
 * ВАЖНО про иконки: [RadioStation.iconRes] у всех станций по умолчанию `null` — своих
 * логотипов в проекте нет (см. обсуждение авторских прав/товарных знаков). Пока иконка не
 * задана, отображается генерируемая цветная "обложка". Когда получите права на использование
 * логотипов конкретных станций, добавьте PNG/SVG в res/drawable и укажите ресурс в iconRes.
 */
object RadioStationsRepository {

    val stations: List<RadioStation> = listOf(
        RadioStation(
            id = "europa_plus",
            name = "Европа Плюс",
            genre = "Хиты, Поп",
            category = StationCategory.POP,
            streamUrl = "https://ep128.streamr.ru/ep128.mp3",
            accentColor = StationPalette[0],
            iconRes = null,
        ),
        RadioStation(
            id = "russkoe_radio",
            name = "Русское Радио",
            genre = "Русская музыка",
            category = StationCategory.POP,
            streamUrl = "https://rusradio.hostingradio.ru/rusradio-128.mp3",
            accentColor = StationPalette[1],
            iconRes = null,
        ),
        RadioStation(
            id = "avtoradio",
            name = "Авторадио",
            genre = "Хиты, Разговорное",
            category = StationCategory.TALK,
            streamUrl = "https://avtoradio.hostingradio.ru/avtoradio128.mp3",
            accentColor = StationPalette[2],
            iconRes = null,
        ),
        RadioStation(
            id = "dorognoe",
            name = "Дорожное радио",
            genre = "Русские хиты",
            category = StationCategory.POP,
            streamUrl = "https://dorognoe.hostingradio.ru/dorognoe96.aacp",
            accentColor = StationPalette[3],
            iconRes = null,
        ),
        RadioStation(
            id = "retro_fm",
            name = "Ретро FM",
            genre = "Ретро, 80-90е",
            category = StationCategory.RETRO,
            streamUrl = "https://retro.hostingradio.ru/retro128.mp3",
            accentColor = StationPalette[4],
            iconRes = null,
        ),
        RadioStation(
            id = "humor_fm",
            name = "Юмор FM",
            genre = "Юмор, Разговорное",
            category = StationCategory.HUMOR,
            streamUrl = "https://umor.hostingradio.ru/umor128.mp3",
            accentColor = StationPalette[5],
            iconRes = null,
        ),
        RadioStation(
            id = "comedy_radio",
            name = "Comedy Radio",
            genre = "Юмор",
            category = StationCategory.HUMOR,
            streamUrl = "https://comedyradio.hostingradio.ru/comedyradio128.mp3",
            accentColor = StationPalette[6],
            iconRes = null,
        ),
        RadioStation(
            id = "chanson",
            name = "Радио Шансон",
            genre = "Шансон",
            category = StationCategory.CHANSON,
            streamUrl = "https://chanson.hostingradio.ru/chanson-128.mp3",
            accentColor = StationPalette[7],
            iconRes = null,
        ),
        RadioStation(
            id = "nashe_radio",
            name = "Наше Радио",
            genre = "Русский рок",
            category = StationCategory.ROCK,
            streamUrl = "https://nashe1.hostingradio.ru/nashe-128.mp3",
            accentColor = StationPalette[0],
            iconRes = null,
        ),
        RadioStation(
            id = "dfm",
            name = "DFM",
            genre = "Танцевальная",
            category = StationCategory.DANCE,
            streamUrl = "https://dfm.hostingradio.ru/dfm-128.mp3",
            accentColor = StationPalette[1],
            iconRes = null,
        ),
        RadioStation(
            id = "radio_dacha",
            name = "Радио Дача",
            genre = "Поп, Русские хиты",
            category = StationCategory.POP,
            streamUrl = "https://dacha.hostingradio.ru/dacha-128.mp3",
            accentColor = StationPalette[2],
            iconRes = null,
        ),
        RadioStation(
            id = "hit_fm",
            name = "Хит FM",
            genre = "Современные хиты",
            category = StationCategory.POP,
            streamUrl = "https://hitfm.hostingradio.ru/hitfm128.mp3",
            accentColor = StationPalette[3],
            iconRes = null,
        ),
        RadioStation(
            id = "mayak",
            name = "Радио Маяк",
            genre = "Новости, Разговорное",
            category = StationCategory.NEWS,
            streamUrl = "https://icecast-vgtrk.cdnvideo.ru/mayakfm_mp3_128kbps",
            accentColor = StationPalette[4],
            iconRes = null,
        ),
        RadioStation(
            id = "vesti_fm",
            name = "Вести FM",
            genre = "Новости",
            category = StationCategory.NEWS,
            streamUrl = "https://icecast-vgtrk.cdnvideo.ru/vestifm_mp3_128kbps",
            accentColor = StationPalette[5],
            iconRes = null,
        ),
        RadioStation(
            id = "radio_rossii",
            name = "Радио России",
            genre = "Новости, Культура",
            category = StationCategory.NEWS,
            streamUrl = "https://icecast-vgtrk.cdnvideo.ru/radiorossii_mp3_128kbps",
            accentColor = StationPalette[6],
            iconRes = null,
        ),
        RadioStation(
            id = "police_wave",
            name = "Милицейская волна",
            genre = "Разговорное, Хиты",
            category = StationCategory.TALK,
            streamUrl = "https://police.hostingradio.ru/police-128.mp3",
            accentColor = StationPalette[7],
            iconRes = null,
        ),
        RadioStation(
            id = "sport_fm",
            name = "Спорт FM",
            genre = "Спорт, Новости",
            category = StationCategory.SPORT,
            streamUrl = "https://sportfm.hostingradio.ru/sportfm128.mp3",
            accentColor = StationPalette[0],
            iconRes = null,
        ),
    )

    fun findById(id: String): RadioStation? = stations.firstOrNull { it.id == id }
}
