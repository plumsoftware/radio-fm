package ru.plumsoftware.radiofm.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaStyleNotificationHelper
import ru.plumsoftware.radiofm.MainActivity
import ru.plumsoftware.radiofm.R
import ru.plumsoftware.radiofm.data.RadioStationsRepository
import ru.plumsoftware.radiofm.model.RadioStation
import ru.plumsoftware.radiofm.widget.WidgetUpdater
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import android.util.Log
import kotlin.collections.indexOfFirst

/**
 * Foreground-сервис воспроизведения радио.
 *
 * Здесь и только здесь живёт ExoPlayer — раньше он создавался прямо в `RadioPlayerManager`
 * на уровне Application, из-за чего поток гарантированно обрывался вскоре после сворачивания
 * приложения (ограничения фона Android). Сервис:
 *  1. Держит ExoPlayer и MediaSession (последнее даёт управление с экрана блокировки,
 *     Bluetooth-гарнитур и Android Auto бесплатно).
 *  2. Публикует любое изменение состояния в [PlaybackStateHolder] — единственный источник
 *     истины, который читают экраны приложения (через RadioPlayerManager.state) и виджет.
 *  3. Рисует уведомление и держит его несворачиваемым (см. buildNotification): свайпом его
 *     закрыть нельзя, кнопки "закрыть" в нём нет. Единственный способ убрать уведомление —
 *     явно остановить воспроизведение из приложения/виджета (см. RadioPlayerManager.stop()),
 *     либо системные способы (принудительная остановка приложения, блокировка уведомлений
 *     в настройках ОС) — их обойти нельзя и не нужно пытаться.
 */
class RadioPlaybackService : Service() {

    companion object {
        private const val TAG = "RadioPlaybackService"
        private const val CHANNEL_ID = "radio_playback"
        private const val NOTIFICATION_ID = 42

        private const val ACTION_NEXT = "ru.plumsoftware.radiofm.action.NEXT"
        private const val ACTION_PREV = "ru.plumsoftware.radiofm.action.PREV"

        private const val ACTION_PLAY = "ru.plumsoftware.radiofm.action.PLAY"
        private const val ACTION_TOGGLE = "ru.plumsoftware.radiofm.action.TOGGLE"
        private const val ACTION_STOP = "ru.plumsoftware.radiofm.action.STOP"
        private const val EXTRA_STATION_ID = "extra_station_id"

        fun playIntent(context: Context, stationId: String): Intent =
            Intent(context, RadioPlaybackService::class.java)
                .setAction(ACTION_PLAY)
                .putExtra(EXTRA_STATION_ID, stationId)

        fun toggleIntent(context: Context): Intent =
            Intent(context, RadioPlaybackService::class.java).setAction(ACTION_TOGGLE)

        fun stopIntent(context: Context): Intent =
            Intent(context, RadioPlaybackService::class.java).setAction(ACTION_STOP)
    }

    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null
    private var currentStation: RadioStation? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: action=${intent?.action} extras=${intent?.extras}")
        when (intent?.action) {
            ACTION_PLAY -> {
                val stationId = intent.getStringExtra(EXTRA_STATION_ID)
                val station = RadioStationsRepository.findById(stationId.orEmpty())
                Log.d(TAG, "ACTION_PLAY: stationId=$stationId found=${station != null} url=${station?.streamUrl}")
                if (station != null) startPlayback(station)
            }

            ACTION_TOGGLE -> toggle()

            ACTION_STOP -> {
                stopPlaybackAndService()
                return START_NOT_STICKY
            }

            ACTION_NEXT -> playAdjacent(forward = true)
            ACTION_PREV -> playAdjacent(forward = false)
        }
        return START_NOT_STICKY
    }

    private fun playAdjacent(forward: Boolean) {
        val current = currentStation ?: return
        val stations = RadioStationsRepository.stations
        val currentIndex = stations.indexOfFirst { it.id == current.id }
        if (currentIndex == -1) return
        val nextIndex = if (forward) {
            (currentIndex + 1) % stations.size
        } else {
            (currentIndex - 1 + stations.size) % stations.size
        }
        startPlayback(stations[nextIndex])
    }

    @OptIn(UnstableApi::class)
    private fun ensurePlayer(): ExoPlayer {
        var p = player
        if (p == null) {
            val httpDataSourceFactory = DefaultHttpDataSource.Factory()
                .setUserAgent("Mozilla/5.0 (Linux; Android 14; RadioFM) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0 Mobile Safari/537.36")
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(15_000)
                .setReadTimeoutMs(15_000)

            p = ExoPlayer.Builder(applicationContext)
                .setMediaSourceFactory(DefaultMediaSourceFactory(httpDataSourceFactory))
                .build()
            p.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    val name = when (playbackState) {
                        Player.STATE_IDLE -> "IDLE"
                        Player.STATE_BUFFERING -> "BUFFERING"
                        Player.STATE_READY -> "READY"
                        Player.STATE_ENDED -> "ENDED"
                        else -> "UNKNOWN($playbackState)"
                    }
                    Log.d(TAG, "onPlaybackStateChanged: $name isPlaying=${p.isPlaying}")
                    refreshState()
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    Log.d(TAG, "onIsPlayingChanged: $isPlaying")
                    refreshState()
                }

                override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                    val title = mediaMetadata.title?.toString() ?: mediaMetadata.artist?.toString()
                    if (title != null) {
                        PlaybackStateHolder.update { it.copy(nowPlayingTitle = title) }
                        pushNotificationAndWidget()
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    Log.e(TAG, "onPlayerError: errorCode=${error.errorCode} errorCodeName=${error.errorCodeName}", error)
                    var cause: Throwable? = error.cause
                    var depth = 0
                    while (cause != null) {
                        Log.e(TAG, "  caused by [$depth]: ${cause::class.java.name}: ${cause.message}")
                        cause = cause.cause
                        depth++
                    }
                    PlaybackStateHolder.update { it.copy(status = PlaybackStatus.ERROR) }
                    pushNotificationAndWidget()
                }
            })
            mediaSession = MediaSession.Builder(applicationContext, p)
                .setSessionActivity(openAppPendingIntent())
                .build()
            player = p
        }
        return p
    }

    private fun startPlayback(station: RadioStation) {
        val p = ensurePlayer()
        Log.d(TAG, "startPlayback: station=${station.id} url=${station.streamUrl} alreadyCurrent=${currentStation?.id == station.id}")
        if (currentStation?.id != station.id) {
            currentStation = station
            PlaybackStateHolder.set(
                PlaybackState(
                    stationId = station.id,
                    status = PlaybackStatus.BUFFERING,
                    nowPlayingTitle = station.name,
                )
            )
            p.setMediaItem(
                MediaItem.Builder()
                    .setUri(station.streamUrl)
                    .setMediaMetadata(MediaMetadata.Builder().setTitle(station.name).build())
                    .build()
            )
            p.prepare()
        }
        p.playWhenReady = true
        // startForeground нужно вызвать быстро после старта сервиса (в течение нескольких
        // секунд), поэтому делаем это сразу, не дожидаясь колбэков плеера.
        startForeground(NOTIFICATION_ID, buildNotification())
    }

    private fun toggle() {
        val p = player ?: return
        if (currentStation == null) return
        p.playWhenReady = !(p.isPlaying)
        // Итоговое состояние выставит refreshState() из слушателя плеера.
    }

    private fun refreshState() {
        val p = player ?: return
        val status = when {
            p.playbackState == Player.STATE_BUFFERING -> PlaybackStatus.BUFFERING
            p.isPlaying -> PlaybackStatus.PLAYING
            p.playbackState == Player.STATE_READY && !p.isPlaying -> PlaybackStatus.PAUSED
            else -> PlaybackStateHolder.state.value.status
        }
        PlaybackStateHolder.update { it.copy(status = status) }
        pushNotificationAndWidget()
    }

    /** Единая точка обновления: и уведомление, и виджет всегда обновляются вместе,
     *  из одного и того же [PlaybackStateHolder] — это и есть полная синхронизация. */
    private fun pushNotificationAndWidget() {
        if (currentStation != null) {
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, buildNotification())
        }
        WidgetUpdater.updateAll(applicationContext)
    }

    private fun buildNotification(): Notification {
        val state = PlaybackStateHolder.state.value
        val isPlayingOrBuffering =
            state.status == PlaybackStatus.PLAYING || state.status == PlaybackStatus.BUFFERING

        val prevAction = NotificationCompat.Action(
            R.drawable.ic_skip_previous, "Предыдущая станция", servicePendingIntent(ACTION_PREV),
        )
        val playPauseAction = NotificationCompat.Action(
            if (isPlayingOrBuffering) R.drawable.ic_pause else R.drawable.ic_play,
            if (isPlayingOrBuffering) "Пауза" else "Играть",
            servicePendingIntent(ACTION_TOGGLE),
        )
        val nextAction = NotificationCompat.Action(
            R.drawable.ic_skip_next, "Следующая станция", servicePendingIntent(ACTION_NEXT),
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.radio_icon)
            .setContentTitle(currentStation?.name ?: getString(R.string.app_name))
            .setContentText(
                when (state.status) {
                    PlaybackStatus.BUFFERING -> "Загрузка…"
                    PlaybackStatus.ERROR -> "Ошибка воспроизведения потока"
                    else -> state.nowPlayingTitle ?: currentStation?.genre.orEmpty()
                }
            )
            .setContentIntent(openAppPendingIntent())
            .addAction(prevAction)
            .addAction(playPauseAction)
            .addAction(nextAction)
            // Обычное (не MediaStyle) ongoing-уведомление Android гарантированно не даёт
            // смахнуть свайпом — этот платформенный баг относится только к специальному
            // медиа-шаблону.
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            // На всякий случай оставляю: если какой-то OEM всё же даст его закрыть —
            // это будет воспринято как команда "стоп".
            .setDeleteIntent(servicePendingIntent(ACTION_STOP))
            .build()
    }

    private fun openAppPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        return PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun servicePendingIntent(action: String): PendingIntent {
        val intent = Intent(this, RadioPlaybackService::class.java).setAction(action)
        return PendingIntent.getService(
            this, action.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun stopPlaybackAndService() {
        player?.stop()
        currentStation = null
        PlaybackStateHolder.set(PlaybackState())
        WidgetUpdater.updateAll(applicationContext)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java) ?: return
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Радио — воспроизведение",
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = "Уведомление о текущей радиостанции и управление воспроизведением"
            setShowBadge(false)
        }
        manager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        player?.release()
        player = null
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }
}
