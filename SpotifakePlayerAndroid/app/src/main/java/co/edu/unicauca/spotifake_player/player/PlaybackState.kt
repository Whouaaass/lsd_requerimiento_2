package co.edu.unicauca.spotifake_player.player

/**
 * States for the playback of the audio.
 */
sealed class PlaybackState {
    object IDLE : PlaybackState()
    object PLAYING : PlaybackState()
    object PAUSED : PlaybackState()
    object STOPPED : PlaybackState()
    object WAITING_FOR_DATA : PlaybackState()
    data class ERROR(val message: String) : PlaybackState()
}