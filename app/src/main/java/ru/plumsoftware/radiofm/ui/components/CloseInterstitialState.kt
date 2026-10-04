package ru.plumsoftware.radiofm.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader

private const val CLOSE_INTERSTITIAL_AD_UNIT_ID = "R-M-19798655-2"

/**
 * Межстраничная реклама «на закрытие экрана».
 *
 * Загружается один раз — когда экран появился в композиции, и показывается один раз —
 * когда экран закрывают. Если к моменту закрытия реклама не успела загрузиться
 * (или загрузка упала), экран закрывается сразу, без ожидания.
 */
@Stable
class CloseInterstitialState internal constructor() {

    private var loader: InterstitialAdLoader? = null
    private var ad: InterstitialAd? = null
    private var loadStarted = false
    private var released = false
    private var closing = false

    internal fun load(appContext: Context, adUnitId: String) {
        if (loadStarted) return
        loadStarted = true

        val adLoader = InterstitialAdLoader(appContext)
        loader = adLoader
        adLoader.loadAd(
            AdRequest.Builder(adUnitId).build(),
            object : InterstitialAdLoadListener {
                override fun onAdLoaded(ad: InterstitialAd) {
                    // Экран могли закрыть раньше, чем пришёл ответ.
                    if (!released) this@CloseInterstitialState.ad = ad
                }

                override fun onAdFailedToLoad(adRequestError: AdRequestError) {
                    // Повторно не грузим: Яндекс прямо не рекомендует ретраи отсюда.
                }
            },
        )
    }

    /**
     * Показывает рекламу (если она готова) и после её закрытия вызывает [onFinished].
     * [onAdOpening] и [onAdClosed] вызываются только если реклама действительно показывается.
     * Повторные вызовы игнорируются, поэтому двойной тап «Назад» не закроет два экрана.
     */
    fun showThen(
        activity: Activity?,
        onAdOpening: () -> Unit = {},
        onAdClosed: () -> Unit = {},
        onFinished: () -> Unit,
    ) {
        if (closing) return
        closing = true

        val loadedAd = ad
        if (loadedAd == null || activity == null) {
            onFinished()
            return
        }

        // Вызывается прямо перед показом — чтобы звук приложения не наложился на рекламу
        // даже на долю секунды. onAdClosed гарантированно придёт в ответ: и после
        // закрытия рекламы, и если показ не удался.
        onAdOpening()

        loadedAd.setAdEventListener(object : InterstitialAdEventListener {
            override fun onAdShown() = Unit

            override fun onAdFailedToShow(adError: AdError) = finish()

            override fun onAdDismissed() = finish()

            override fun onAdClicked() = Unit

            override fun onAdImpression(impressionData: ImpressionData?) = Unit

            private fun finish() {
                loadedAd.setAdEventListener(null)
                ad = null
                onAdClosed()
                onFinished()
            }
        })
        loadedAd.show(activity)
    }

    internal fun release() {
        released = true
        ad?.setAdEventListener(null)
        ad = null
        loader = null
    }
}

@Composable
fun rememberCloseInterstitial(
    adUnitId: String = CLOSE_INTERSTITIAL_AD_UNIT_ID,
): CloseInterstitialState {
    val appContext = LocalContext.current.applicationContext
    val state = remember(adUnitId) { CloseInterstitialState() }

    DisposableEffect(state) {
        state.load(appContext, adUnitId)
        onDispose { state.release() }
    }
    return state
}

internal tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}