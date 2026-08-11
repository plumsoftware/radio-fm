package ru.plumsoftware.radiofm

import android.app.Application
import ru.plumsoftware.radiofm.player.RadioPlayerManager
import com.yandex.mobile.ads.common.YandexAds

class RadioFmApplication : Application() {

    val radioPlayerManager: RadioPlayerManager by lazy { RadioPlayerManager(this) }

    override fun onCreate() {
        super.onCreate()

        // Здесь, до инициализации SDK, можно настроить политику обработки
        // персональных данных пользователя (GDPR/COPPA), если это нужно вашему приложению.
        // См. https://ads.yandex.com/helpcenter/ru/dev/android/gdpr

        YandexAds.initialize(this) {
            // SDK готов к показу рекламы.
        }
    }
}
