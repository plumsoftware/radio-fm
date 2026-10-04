package ru.plumsoftware.radiofm.player

/**
 * Громкость плеера внутри приложения (0f..1f), общая для экранов и [RadioPlaybackService].
 *
 * Устроен так же, как [PlaybackStateHolder], только в обратную сторону: экран пишет сюда
 * значение через [RadioPlayerManager.setVolume], а сервис применяет его к своему плееру.
 * Системную громкость устройства это не трогает.
 *
 * Все обращения — с главного потока.
 */
object PlaybackVolumeHolder {

    var volume: Float = 1f
        private set

    private var listener: ((Float) -> Unit)? = null

    fun set(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        this.volume = clamped
        listener?.invoke(clamped)
    }

    /**
     * Вызывает сервис, когда плеер создан. Текущее значение применяется сразу, поэтому
     * неважно, что произошло раньше — создание плеера или вызов [set].
     */
    fun attach(listener: (Float) -> Unit) {
        this.listener = listener
        listener(volume)
    }

    /** Вызывает сервис перед освобождением плеера. */
    fun detach() {
        listener = null
    }
}