package co.edu.unicauca.spotifake_player.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class AudioPlayer(context: Context) {
    private var exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.IDLE)
    val playbackState: StateFlow<PlaybackState> = _playbackState

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_IDLE -> _playbackState.value = PlaybackState.IDLE
                    Player.STATE_BUFFERING -> _playbackState.value = PlaybackState.WAITING_FOR_DATA
                    Player.STATE_READY -> {
                        // We are ready, but might be paused
                        if (exoPlayer.playWhenReady) {
                            _playbackState.value = PlaybackState.PLAYING
                        } else {
                            _playbackState.value = PlaybackState.PAUSED
                        }
                    }
                    Player.STATE_ENDED -> _playbackState.value = PlaybackState.STOPPED
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (exoPlayer.playbackState == Player.STATE_READY) {
                    if (isPlaying) {
                        _playbackState.value = PlaybackState.PLAYING
                    } else {
                        _playbackState.value = PlaybackState.PAUSED
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                _playbackState.value = PlaybackState.ERROR("ExoPlayer error: ${error.message}")
            }
        })
    }

    /**
     * Prepare playback from a local file.
     */
    fun prepare(audioFile: File, autoPlay: Boolean = true) {
        stop() // Clear previous player state
        val mediaItem = MediaItem.fromUri(audioFile.toURI().toString())
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.playWhenReady = autoPlay
        exoPlayer.prepare()
    }

    /**
     * Prepare playback from our custom gRPC streaming DataSource.
     */
    @OptIn(UnstableApi::class)
    fun prepare(dataSourceFactory: DataSource.Factory, autoPlay: Boolean = true) {
        stop() // Clear previous player state

        // A MediaSource that reads from our custom factory
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri("grpc://stream")) // URI is arbitrary

        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.playWhenReady = autoPlay
        exoPlayer.prepare()
    }

    fun start() {
        exoPlayer.play()
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun stop() {
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        _playbackState.value = PlaybackState.STOPPED
    }

    fun isPlaying(): Boolean {
        return exoPlayer.isPlaying
    }

    fun getCurrentPosition(): Int {
        return exoPlayer.currentPosition.toInt()
    }

    fun getDuration(): Int {
        val duration = exoPlayer.duration
        return if (duration == -9223372036854775807L) 0 else duration.toInt() // C.TIME_UNSET
    }

    fun release() {
        exoPlayer.release()
    }
}