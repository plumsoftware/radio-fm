# Радио FM

Android-приложение интернет-радио на **Kotlin + Jetpack Compose**.

## Стек

- **Jetpack Compose** (Material 3) — весь UI
- **Navigation Compose** — переход между экраном списка станций и экраном плеера
- **Media3 / ExoPlayer** — потоковое воспроизведение интернет-радио (icecast/shoutcast/mp3/aac)
- **DataStore Preferences** — хранение выбранной темы между запусками
- **Yandex Mobile Ads SDK 8.2.0** — адаптивный sticky-баннер внизу экрана плеера

## Структура проекта

```
app/src/main/java/com/example/radiofm/
├── MainActivity.kt              — точка входа, подключает тему и NavGraph
├── MainViewModel.kt             — состояние выбранной темы (System/Light/Dark)
├── RadioFmApplication.kt        — инициализация Yandex Ads SDK, синглтон плеера
├── ViewModelFactories.kt
├── data/
│   ├── RadioStationsRepository.kt   — список станций (см. ниже)
│   └── ThemePreferences.kt          — DataStore-обёртка для темы
├── model/RadioStation.kt
├── player/
│   ├── RadioPlayerManager.kt    — обёртка над ExoPlayer, живёт на уровне Application
│   └── PlaybackState.kt
├── navigation/
│   ├── Screen.kt
│   └── RadioFmNavGraph.kt
└── ui/
    ├── theme/                   — Color.kt / Theme.kt / Type.kt (синий primary, 2 темы)
    ├── components/
    │   ├── StationAvatar.kt     — генерируемая цветная "обложка" станции
    │   └── StickyBannerAd.kt    — обёртка Yandex BannerAdView (sticky-баннер)
    └── screens/
        ├── list/RadioListScreen.kt      — экран со списком станций + поиск
        └── player/PlayerScreen.kt       — экран плеера с баннером внизу
```

## Про логотипы станций

В скриншотах-референсах используются логотипы реальных радиостанций (NRJ, DFM, Like FM,
Радио Disney и т.д.) — это защищённые товарные знаки/логотипы, и они не включены в проект.
Вместо них каждая станция получает сгенерированную цветную "обложку" с первой буквой названия
(`StationAvatar.kt`). Замените её на `Image`/Coil с вашими собственными лицензированными
логотипами станций, когда будете подключать реальный контент-провайдер.

## Список станций и потоки

`RadioStationsRepository.kt` содержит демонстрационный список из публичных общедоступных
интернет-радиостанций (SomaFM, Radio Paradise) — чтобы приложение реально воспроизводило звук
сразу после сборки. Замените на свой список станций / получение с backend.

## Подключение Yandex Mobile Ads SDK

Интеграция сделана по официальным гайдам:
- Быстрый старт: https://ads.yandex.com/helpcenter/ru/dev/android/quick-start
- Адаптивный sticky-баннер: https://ads.yandex.com/helpcenter/ru/dev/android/adaptive-sticky-banner

Что уже настроено:
1. Зависимость `com.yandex.android:mobileads:8.2.0` в `app/build.gradle.kts`.
2. Ручная инициализация SDK в `RadioFmApplication.onCreate()` (`YandexAds.initialize(...)`),
   автоматическая инициализация отключена через meta-data в `AndroidManifest.xml`.
3. `StickyBannerAd.kt` — Composable-обёртка над `BannerAdView`, вычисляет ширину контейнера
   и загружает баннер через `BannerAdSize.sticky(context, adWidth)`, закреплена в
   `PlayerScreen` через `Scaffold(bottomBar = { StickyBannerAd() })`.

**Перед публикацией в сторе обязательно замените демонстрационный `adUnitId`:**

```kotlin
// ui/components/StickyBannerAd.kt
fun StickyBannerAd(
    modifier: Modifier = Modifier,
    adUnitId: String = "demo-banner-yandex", // <-- замените на свой реальный ID
)
```

Свой `adUnitId` вы получите в личном кабинете Рекламной сети Яндекса после регистрации
приложения (см. раздел "Настройте приложение в своём аккаунте" в гайде быстрого старта).

## Темы

Приложение поддерживает светлую и тёмную темы с единым синим акцентным цветом
(`#2F6FED` в светлой / `#6A9BFF` в тёмной, см. `ui/theme/Color.kt`). Режим переключается
иконкой в правом верхнем углу экрана списка станций (System → Light → Dark → ...), выбор
сохраняется между запусками через DataStore.

## Сборка

1. Откройте папку проекта в Android Studio (Koala/Ladybug и новее).
2. Дождитесь синхронизации Gradle (потребуется интернет — зависимости подтягиваются из
   `google()` и `mavenCentral()`).
3. Запустите на устройстве/эмуляторе с Android 8.0 (API 26) и выше.

## Известные упрощения (что стоит доработать для продакшена)

- Воспроизведение не вынесено в `MediaSessionService`/`foreground service` — при полном
  закрытии приложения (свайп из "недавних") звук останавливается. Для полноценного фонового
  воспроизведения с уведомлением и управлением с экрана блокировки стоит добавить
  `androidx.media3:media3-session` `MediaSessionService`.
- Избранное (`isFavorite`) и история прослушивания — только UI-заглушки, без сохранения.
- Список станций — статический `object`, а не подключение к backend/API.
- Обложки станций сгенерированы программно (см. раздел выше про логотипы).
