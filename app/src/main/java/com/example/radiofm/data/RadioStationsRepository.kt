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
            id = "groove_salad",
            name = "Groove Salad",
            genre = "Downtempo, Chillout",
            streamUrl = "https://ice1.somafm.com/groovesalad-128-mp3",
            accentColor = StationPalette[0],
        ),
        RadioStation(
            id = "indie_pop",
            name = "Indie Pop Rocks",
            genre = "Indie, Pop",
            streamUrl = "https://ice1.somafm.com/indiepop-128-mp3",
            accentColor = StationPalette[1],
        ),
        RadioStation(
            id = "beat_blender",
            name = "Beat Blender",
            genre = "Deep House, Downtempo",
            streamUrl = "https://ice1.somafm.com/beatblender-128-mp3",
            accentColor = StationPalette[2],
        ),
        RadioStation(
            id = "drone_zone",
            name = "Drone Zone",
            genre = "Ambient",
            streamUrl = "https://ice1.somafm.com/dronezone-128-mp3",
            accentColor = StationPalette[3],
        ),
        RadioStation(
            id = "space_station",
            name = "Space Station Soma",
            genre = "Space Music",
            streamUrl = "https://ice1.somafm.com/spacestation-128-mp3",
            accentColor = StationPalette[4],
        ),
        RadioStation(
            id = "lush",
            name = "Lush",
            genre = "Vocal, Chillout",
            streamUrl = "https://ice1.somafm.com/lush-128-mp3",
            accentColor = StationPalette[5],
        ),
        RadioStation(
            id = "radio_paradise_main",
            name = "Radio Paradise",
            genre = "Eclectic Mix",
            streamUrl = "https://stream.radioparadise.com/mp3-192",
            accentColor = StationPalette[6],
        ),
        RadioStation(
            id = "radio_paradise_mellow",
            name = "RP Mellow Mix",
            genre = "Mellow, Acoustic",
            streamUrl = "https://stream.radioparadise.com/mellow-192",
            accentColor = StationPalette[7],
        ),
        RadioStation(
            id = "radio_paradise_rock",
            name = "RP Rock Mix",
            genre = "Rock",
            streamUrl = "https://stream.radioparadise.com/rock-192",
            accentColor = StationPalette[0],
        ),
        RadioStation(
            id = "seventies",
            name = "Left Coast 70s",
            genre = "70s Rock",
            streamUrl = "https://ice1.somafm.com/seventies-128-mp3",
            accentColor = StationPalette[1],
        ),
        RadioStation(
            id = "poptron",
            name = "PopTron",
            genre = "Electropop, Indie",
            streamUrl = "https://ice1.somafm.com/poptron-128-mp3",
            accentColor = StationPalette[2],
        ),
        RadioStation(
            id = "secret_agent",
            name = "Secret Agent",
            genre = "Downtempo, Spy Lounge",
            streamUrl = "https://ice1.somafm.com/secretagent-128-mp3",
            accentColor = StationPalette[3],
        ),
    )

    fun findById(id: String): RadioStation? = stations.firstOrNull { it.id == id }
}
