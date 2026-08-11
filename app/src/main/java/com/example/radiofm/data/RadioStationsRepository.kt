package com.example.radiofm.data

import com.example.radiofm.model.RadioStation
import com.example.radiofm.ui.theme.StationPalette

/**
 * Простой in-memory источник станций.
 *
 * Здесь используются публичные общедоступные интернет-потоки (SomaFM, Radio Paradise),
 * чтобы приложение реально воспроизводило звук "из коробки". В боевом приложении
 * этот список стоит заменить на свои станции / получать с backend.
 */
object RadioStationsRepository {

    val stations: List<RadioStation> = listOf(
        RadioStation(
            id = "europa_plus",
            name = "Европа Плюс",
            genre = "Хиты, Поп",
            streamUrl = "https://ep128.streamr.ru/ep128.mp3",
            accentColor = StationPalette[0],
        ),
        RadioStation(
            id = "russkoe_radio",
            name = "Русское Радио",
            genre = "Русская музыка",
            streamUrl = "https://rusradio.hostingradio.ru/rusradio-128.mp3",
            accentColor = StationPalette[1],
        ),
        RadioStation(
            id = "avtoradio",
            name = "Авторадио",
            genre = "Хиты, Разговорное",
            streamUrl = "https://avtoradio.hostingradio.ru/avtoradio128.mp3",
            accentColor = StationPalette[2],
        ),
        RadioStation(
            id = "dorognoe",
            name = "Дорожное радио",
            genre = "Русские хиты",
            streamUrl = "https://dorognoe.hostingradio.ru/dorognoe96.aacp",
            accentColor = StationPalette[3],
        ),
        RadioStation(
            id = "retro_fm",
            name = "Ретро FM",
            genre = "Ретро, 80-90е",
            streamUrl = "https://retro.hostingradio.ru/retro128.mp3",
            accentColor = StationPalette[4],
        ),
        RadioStation(
            id = "humor_fm",
            name = "Юмор FM",
            genre = "Юмор, Разговорное",
            streamUrl = "https://umor.hostingradio.ru/umor128.mp3",
            accentColor = StationPalette[5],
        ),
        RadioStation(
            id = "comedy_radio",
            name = "Comedy Radio",
            genre = "Юмор",
            streamUrl = "https://comedyradio.hostingradio.ru/comedyradio128.mp3",
            accentColor = StationPalette[6],
        ),
        RadioStation(
            id = "chanson",
            name = "Радио Шансон",
            genre = "Шансон",
            streamUrl = "https://chanson.hostingradio.ru/chanson-128.mp3",
            accentColor = StationPalette[7],
        ),
        RadioStation(
            id = "nashe_radio",
            name = "Наше Радио",
            genre = "Русский рок",
            streamUrl = "https://nashe1.hostingradio.ru/nashe-128.mp3",
            accentColor = StationPalette[0],
        ),
        RadioStation(
            id = "dfm",
            name = "DFM",
            genre = "Танцевальная",
            streamUrl = "https://dfm.hostingradio.ru/dfm-128.mp3",
            accentColor = StationPalette[1],
        ),
        RadioStation(
            id = "radio_dacha",
            name = "Радио Дача",
            genre = "Поп, Русские хиты",
            streamUrl = "https://dacha.hostingradio.ru/dacha-128.mp3",
            accentColor = StationPalette[2],
        ),
        RadioStation(
            id = "hit_fm",
            name = "Хит FM",
            genre = "Современные хиты",
            streamUrl = "https://hitfm.hostingradio.ru/hitfm128.mp3",
            accentColor = StationPalette[3],
        ),
        RadioStation(
            id = "mayak",
            name = "Радио Маяк",
            genre = "Новости, Разговорное",
            streamUrl = "https://icecast-vgtrk.cdnvideo.ru/mayakfm_mp3_128kbps",
            accentColor = StationPalette[4],
        ),
        RadioStation(
            id = "vesti_fm",
            name = "Вести FM",
            genre = "Новости",
            streamUrl = "https://icecast-vgtrk.cdnvideo.ru/vestifm_mp3_128kbps",
            accentColor = StationPalette[5],
        ),
        RadioStation(
            id = "radio_rossii",
            name = "Радио России",
            genre = "Новости, Культура",
            streamUrl = "https://icecast-vgtrk.cdnvideo.ru/radiorossii_mp3_128kbps",
            accentColor = StationPalette[6],
        ),
        RadioStation(
            id = "milicейская_volna",
            name = "Милицейская волна",
            genre = "Разговорное, Хиты",
            streamUrl = "https://police.hostingradio.ru/police-128.mp3",
            accentColor = StationPalette[7],
        ),
    )

    fun findById(id: String): RadioStation? = stations.firstOrNull { it.id == id }
}
