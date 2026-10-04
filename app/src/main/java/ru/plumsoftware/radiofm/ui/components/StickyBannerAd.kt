package ru.plumsoftware.radiofm.ui.components

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import ru.plumsoftware.radiofm.BuildConfig
import kotlin.math.roundToInt

/**
 * Адаптивный sticky-баннер Яндекс Рекламной сети, закреплённый внизу экрана списка станций.
 *
 * Реализовано по гайду:
 * https://ads.yandex.com/helpcenter/ru/dev/android/adaptive-sticky-banner
 *
 * Баннер переживает уход экрана из композиции: сам [BannerAdView] создаётся и загружается
 * один раз на Activity и лежит в [BannerAdCache]. Когда экран списка уходит (открыли плеер)
 * и возвращается, здесь не создаётся новый баннер — достаётся уже загруженный и просто
 * вставляется обратно, без повторного запроса рекламы.
 *
 * ВАЖНО: [adUnitId] в debug-сборке — демонстрационный ("demo-banner-yandex"), он гарантированно
 * возвращает тестовое объявление. В release берётся реальный идентификатор рекламного места
 * из BuildConfig.BANNER_AD_UNIT_ID.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun StickyBannerAd(
    modifier: Modifier = Modifier,
    adUnitId: String = if (BuildConfig.DEBUG) "demo-banner-yandex" else BuildConfig.BANNER_AD_UNIT_ID,
) {
    val activity = LocalContext.current.findActivity() as? ComponentActivity ?: return

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        // Ширина рекламного контейнера в dp — используется для расчёта адаптивного размера баннера.
        val adWidthDp = maxWidth.value.roundToInt()
        if (adWidthDp <= 0) return@BoxWithConstraints

        val banner = remember(activity, adWidthDp, adUnitId) {
            BannerAdCache.get(activity, adWidthDp, adUnitId)
        }

        // key: если баннер пришлось пересоздать (изменилась ширина контейнера),
        // AndroidView должен перестроиться и взять новый View, а не держать старый.
        key(banner) {
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = {
                    // После прошлого показа баннер ещё числится ребёнком старого контейнера,
                    // а у View может быть только один родитель — сначала отцепляем.
                    (banner.parent as? ViewGroup)?.removeView(banner)
                    banner
                },
                // onRelease намеренно не задан: destroy() здесь убивал бы баннер при каждом
                // уходе с экрана (так было раньше — отсюда и перезагрузка при возврате
                // из плеера). Теперь его уничтожает BannerAdCache вместе с Activity.
            )
        }
    }
}

/**
 * Хранит один загруженный баннер на Activity. Все обращения — с главного потока.
 */
private object BannerAdCache {

    private var owner: ComponentActivity? = null
    private var banner: BannerAdView? = null
    private var bannerWidthDp: Int = 0
    private var bannerAdUnitId: String? = null

    fun get(activity: ComponentActivity, widthDp: Int, adUnitId: String): BannerAdView {
        banner?.let { cached ->
            if (owner === activity && bannerWidthDp == widthDp && bannerAdUnitId == adUnitId) {
                return cached
            }
        }

        // Баннера ещё нет, либо он от другой Activity / под другую ширину (поворот,
        // split-screen) / под другой рекламный блок — старый выбрасываем и грузим новый.
        destroy()

        val created = BannerAdView(activity).apply {
            setBannerAdEventListener(object : BannerAdEventListener {
                override fun onAdLoaded() {
                    // Баннер успешно загружен и отображается.
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
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
            setAdSize(BannerAdSize.sticky(activity, widthDp))
            // Единственный loadAd на всю жизнь этого баннера.
            loadAd(AdRequest.Builder(adUnitId).build())
        }
        owner = activity
        banner = created
        bannerWidthDp = widthDp
        bannerAdUnitId = adUnitId

        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(lifecycleOwner: LifecycleOwner) {
                if (owner === activity) destroy()
            }
        })
        return created
    }

    private fun destroy() {
        banner?.let { old ->
            (old.parent as? ViewGroup)?.removeView(old)
            old.destroy()
        }
        banner = null
        owner = null
        bannerWidthDp = 0
        bannerAdUnitId = null
    }
}