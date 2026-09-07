package ru.plumsoftware.radiofm.data

import ru.plumsoftware.radiofm.R
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
            iconRes = R.drawable.eplus,
        ),
        RadioStation(
            id = "russkoe_radio",
            name = "Русское Радио",
            genre = "Русская музыка",
            category = StationCategory.POP,
            streamUrl = "http://23.105.238.4/rusradio128.mp3",
            accentColor = StationPalette[1],
            iconRes = R.drawable.rr,
        ),
        RadioStation(
            id = "avtoradio",
            name = "Авторадио",
            genre = "Хиты, Разговорное",
            category = StationCategory.TALK,
            streamUrl = "http://23.105.238.4/gpm-avtoradio495.aacp",
            accentColor = StationPalette[2],
            iconRes = R.drawable.ar,
        ),
        RadioStation(
            id = "dorognoe",
            name = "Дорожное радио",
            genre = "Русские хиты",
            category = StationCategory.POP,
            streamUrl = "http://emgregion.hostingradio.ru:8064/moscow.dorognoe.mp3",
            accentColor = StationPalette[3],
            iconRes = R.drawable.road,
        ),
        RadioStation(
            id = "retro_fm",
            name = "Ретро FM",
            genre = "Ретро, 80-90е",
            category = StationCategory.RETRO,
            streamUrl = "http://emgregion.hostingradio.ru:8064/moscow.retrofm.mp3",
            accentColor = StationPalette[4],
            iconRes = R.drawable.retro,
        ),
        RadioStation(
            id = "humor_fm",
            name = "Юмор FM",
            genre = "Юмор, Разговорное",
            category = StationCategory.HUMOR,
            streamUrl = "http://23.105.238.4/gpm-humorfm495.aacp",
            accentColor = StationPalette[5],
            iconRes = R.drawable.humor,
        ),
        RadioStation(
            id = "comedy_radio",
            name = "Comedy Radio",
            genre = "Юмор",
            category = StationCategory.HUMOR,
            streamUrl = "http://23.105.238.4/gpm-comedyradio495.aacp",
            accentColor = StationPalette[6],
            iconRes = R.drawable.comedy,
        ),
        RadioStation(
            id = "rus_pop_radio",
            name = "POPFM Русское",
            genre = "Поп",
            category = StationCategory.POP,
            streamUrl = "https://stream.popfm-bir.ru:1085/stream",
            accentColor = StationPalette[6],
            iconRes = R.drawable.rus_pop,
        ),
        RadioStation(
            id = "new_radio",
            name = "Новое радио",
            genre = "Поп, Хиты",
            category = StationCategory.POP,
            streamUrl = "http://stream.newradio.ru/moscow.novoe.aacp",
            accentColor = StationPalette[0],
            iconRes = R.drawable.new_radio,
        ),
        RadioStation(
            id = "child_radio",
            name = "Детское радио",
            genre = "Детские песни, Сказки",
            category = StationCategory.KIDS,
            streamUrl = "http://23.105.238.4/gpm-detifm495.aacp",
            accentColor = StationPalette[1],
            iconRes = R.drawable.child_radio,
        ),
//        RadioStation(
//            id = "chanson",
//            name = "Радио Шансон",
//            genre = "Шансон",
//            category = StationCategory.CHANSON,
//            streamUrl = "https://chanson.hostingradio.ru/chanson-128.mp3",
//            accentColor = StationPalette[7],
//            iconRes = null,
//        ),
        RadioStation(
            id = "nashe_radio",
            name = "Наше Радио",
            genre = "Русский рок",
            category = StationCategory.ROCK,
            streamUrl = "http://nashe1.hostingradio.ru/nashe-128.mp3",
            accentColor = StationPalette[0],
            iconRes = R.drawable.nashe,
        ),
        RadioStation(
            id = "dfm",
            name = "DFM",
            genre = "Танцевальная",
            category = StationCategory.DANCE,
            streamUrl = "http://23.105.238.4/dfm128.mp3",
            accentColor = StationPalette[1],
            iconRes = R.drawable.dfm,
        ),
        RadioStation(
            id = "radio_dacha",
            name = "Радио Дача",
            genre = "Поп, Русские хиты",
            category = StationCategory.POP,
            streamUrl = "http://microit.n340.com:9000/VgMv0WV17ZVx1uuo_12_dacha_128_reg_1093",
            accentColor = StationPalette[2],
            iconRes = R.drawable.dacha,
        ),
        RadioStation(
            id = "pervoe_sportivnoe",
            name = "Первое Спортивное",
            genre = "Спорт, Новости",
            category = StationCategory.SPORT,
            streamUrl = "http://microit.n340.com:9000/VgMv0WV17ZVx1uuo_20_sport_128_reg_1",
            accentColor = StationPalette[2],
            iconRes = R.drawable.pervoe_sport,
        ),
        RadioStation(
            id = "hit_fm",
            name = "Хит FM",
            genre = "Современные хиты",
            category = StationCategory.POP,
            streamUrl = "https://hitfm.hostingradio.ru/hitfm128.mp3",
            accentColor = StationPalette[3],
            iconRes = R.drawable.hit_fm,
        ),
        RadioStation(
            id = "mayak",
            name = "Радио Маяк",
            genre = "Новости, Разговорное",
            category = StationCategory.NEWS,
            streamUrl = "http://icecast.vgtrk.cdnvideo.ru/mayakfm_mp3_192kbps",
            accentColor = StationPalette[4],
            iconRes = R.drawable.maiak,
        ),
        RadioStation(
            id = "vesti_fm",
            name = "Вести FM",
            genre = "Новости",
            category = StationCategory.NEWS,
            streamUrl = "https://icecast-vgtrk.cdnvideo.ru/vestifm_mp3_128kbps",
            accentColor = StationPalette[5],
            iconRes = R.drawable.vesti,
        ),
        RadioStation(
            id = "radio_rossii",
            name = "Радио России",
            genre = "Новости, Культура",
            category = StationCategory.NEWS,
            streamUrl = "http://listen.vdfm.ru:8000/radiorussia",
            accentColor = StationPalette[6],
            iconRes = R.drawable.russia,
        ),
        RadioStation(
            id = "police_wave",
            name = "Милицейская волна",
            genre = "Разговорное, Хиты",
            category = StationCategory.TALK,
            streamUrl = "http://radiomv.hostingradio.ru/radiomv256.mp3",
            accentColor = StationPalette[7],
            iconRes = R.drawable.police,
        ),
//        RadioStation(
//            id = "sport_fm",
//            name = "Спорт FM",
//            genre = "Спорт, Новости",
//            category = StationCategory.SPORT,
//            streamUrl = "https://sportfm.hostingradio.ru/sportfm128.mp3",
//            accentColor = StationPalette[0],
//            iconRes = R.drawable.sport,
//        ),
    )

    fun findById(id: String): RadioStation? = stations.firstOrNull { it.id == id }
}
