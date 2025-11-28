package co.edu.unicauca.spotifake_player.viewmodel

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import co.edu.unicauca.spotifake_player.player.AudioPlayer
import co.edu.unicauca.spotifake_player.player.PlaybackState
import co.edu.unicauca.spotifake_player.repository.AudioStreamRepository

class StreamingPlayerViewModel(context: Context) {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val audioPlayer = AudioPlayer(context) // Needs context now
    private val repository = AudioStreamRepository(context)

    // ... [All your StateFlow variables are unchanged] ...
    private val _downloadStatus = MutableStateFlow("")
    val downloadStatus: StateFlow<String> = _downloadStatus
    private val _playingStatus = MutableStateFlow("")
    val playingStatus: StateFlow<String> = _playingStatus
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused
    private val _isDownloading = MutableStateFlow(false)
    val isDownloading: StateFlow<Boolean> = _isDownloading
    private val _downloadProgress = MutableStateFlow(0L)
    val downloadProgress: StateFlow<Long> = _downloadProgress
    private val _currentPosition = MutableStateFlow(0)
    val currentPosition: StateFlow<Int> = _currentPosition
    private val _duration = MutableStateFlow(0)
    val duration: StateFlow<Int> = _duration

    private var monitorJob: Job? = null

    init {
        observePlaybackState()
    }

    private fun observePlaybackState() {
        scope.launch {
            audioPlayer.playbackState.collect { state ->
                when (state) {
                    PlaybackState.PLAYING -> {
                        _isPlaying.value = true
                        _isPaused.value = false
                        _playingStatus.value = "Playing"
                        if (monitorJob == null || !monitorJob!!.isActive) {
                            startMonitoring()
                        }
                    }
                    PlaybackState.PAUSED -> {
                        _isPlaying.value = false
                        _isPaused.value = true
                        _playingStatus.value = "Paused"
                    }
                    PlaybackState.STOPPED -> {
                        _isPlaying.value = false
                        _isPaused.value = false
                        _playingStatus.value = "Stopped"
                        monitorJob?.cancel()
                    }
                    PlaybackState.WAITING_FOR_DATA -> {
                        _playingStatus.value = "Buffering..."
                        _isDownloading.value = true
                    }
                    PlaybackState.IDLE -> {
                        _playingStatus.value = ""
                    }
                    is PlaybackState.ERROR -> {
                        _playingStatus.value = "Error: ${state.message}"
                        handleError(Exception(state.message))
                    }
                }
            }
        }
    }

    fun startDownloadAndPlay() {
        stopPlayback()
        _isDownloading.value = true
        _downloadStatus.value = "Streaming..."
        _playingStatus.value = "Connecting..."
        _downloadProgress.value = 0L

        try {
            // 1. Get the DataSource Factory. This call also
            //    launches the gRPC stream in the background.
            val dataSourceFactory = repository.getStreamingDataSourceFactory(scope) { bytesWritten ->
                // This callback updates the download progress
                _downloadProgress.value = bytesWritten
            }

            // 2. Tell the player to prepare using this factory
            audioPlayer.prepare(dataSourceFactory, autoPlay = true)

        } catch (e: Exception) {
            handleError(e)
        }
    }

    fun playDirectFromFile() {
        stopPlayback()
        _playingStatus.value = "Loading direct file..."

        scope.launch {
            try {
                val file = repository.getDirectFile()
                if (file != null && file.exists()) {
                    _downloadStatus.value = "Playing from file"
                    _downloadProgress.value = file.length()
                    audioPlayer.prepare(file, autoPlay = true)
                } else {
                    _playingStatus.value = "File not found in res/raw"
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun startMonitoring() {
        monitorJob?.cancel()
        monitorJob = scope.launch {
            while (true) {
                delay(250)
                _currentPosition.value = audioPlayer.getCurrentPosition()
                val dur = audioPlayer.getDuration()
                if (dur > 0) {
                    _duration.value = dur
                }
            }
        }
    }

    fun pausePlayback() {
        audioPlayer.pause()
    }

    fun resumePlayback() {
        audioPlayer.start()
    }

    fun stopPlayback() {
        monitorJob?.cancel()
        _isDownloading.value = false

        audioPlayer.stop()
        repository.cleanup() // This will close the pipes

        _downloadStatus.value = ""
        _playingStatus.value = ""
        _isPlaying.value = false
        _isPaused.value = false
        _downloadProgress.value = 0L
        _currentPosition.value = 0
        _duration.value = 0
    }

    private fun handleError(e: Exception) {
        if (_playingStatus.value?.startsWith("Error:") == false) {
            _playingStatus.value = "Error: ${e.message}"
            stopPlayback()
        }
    }

    fun cleanup() {
        stopPlayback()
        audioPlayer.release() // Release ExoPlayer
        scope.cancel()
    }
}