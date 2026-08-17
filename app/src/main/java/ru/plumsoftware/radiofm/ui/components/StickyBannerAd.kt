package ru.plumsoftware.radiofm.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import kotlin.math.roundToInt

/**
 * Адаптивный sticky-баннер Яндекс Рекламной сети, закреплённый внизу экрана плеера.
 *
 * Реализовано по гайду:
 * https://ads.yandex.com/helpcenter/ru/dev/android/adaptive-sticky-banner
 *
 * ВАЖНО: [adUnitId] по умолчанию — демонстрационный ("demo-banner-yandex"), он гарантированно
 * возвращает тестовое объявление. Перед публикацией в сторе замените его на свой
 * реальный идентификатор рекламного места из кабинета Рекламной сети Яндекса.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun StickyBannerAd(
    modifier: Modifier = Modifier,
    adUnitId: String = "demo-banner-yandex",
) {
    val density = LocalDensity.current
    // Запоминаем последнюю ширину, для которой уже была загружена реклама,
    // чтобы не переинициализировать баннер на каждой рекомпозиции.
    val lastLoadedWidth = remember { mutableIntStateOf(-1) }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        // Ширина рекламного контейнера в dp — используется для расчёта адаптивного размера баннера.
        val containerWidthDp = maxWidth
        val adWidthDp = with(density) { containerWidthDp.toPx().toInt() / density.density }.roundToInt()

        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                BannerAdView(context).apply {
                    setBannerAdEventListener(object : BannerAdEventListener {
                        override fun onAdLoaded() {
                            // Баннер успешно загружен и отображается.
                        }

                        override fun onAdFailedToLoad(adRequestError: AdRequestError) {
                            // Загрузка не удалась — по рекомендации SDK не повторяем запрос
                            // автоматически из этого колбэка.
                        }

                        override fun onAdClicked() {
                            // Клик по объявлению.
                        }

                        override fun onImpression(impressionData: ImpressionData?) {
                            // Зафиксирован показ объявления.
                        }
                    })
                }
            },
            update = { bannerAdView ->
                if (adWidthDp > 0 && adWidthDp != lastLoadedWidth.intValue) {
                    lastLoadedWidth.intValue = adWidthDp
                    bannerAdView.setAdSize(BannerAdSize.sticky(bannerAdView.context, adWidthDp))
                    bannerAdView.loadAd(AdRequest.Builder(adUnitId).build())
                }
            },
            onRelease = { bannerAdView ->
                bannerAdView.destroy()
            },
        )
    }
}
