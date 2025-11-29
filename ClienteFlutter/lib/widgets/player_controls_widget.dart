import 'package:flutter/material.dart';
import '../GrpcAudioSource.dart';

class PlayerControlsWidget extends StatelessWidget {
  final String songTitle;
  final String artistName;
  final bool isPlaying;
  final bool isLoading;
  final DownloadProgress? downloadProgress;
  final Duration currentPosition;
  final Duration totalDuration;
  final VoidCallback onPlayPause;
  final Function(Duration) onSeek;

  const PlayerControlsWidget({
    super.key,
    required this.songTitle,
    required this.artistName,
    required this.isPlaying,
    required this.isLoading,
    this.downloadProgress,
    required this.currentPosition,
    required this.totalDuration,
    required this.onPlayPause,
    required this.onSeek,
  });

  // Format bytes to human-readable format
  String _formatBytes(int bytes) {
    if (bytes < 1024) {
      return '$bytes B';
    } else if (bytes < 1024 * 1024) {
      return '${(bytes / 1024).toStringAsFixed(1)} KB';
    } else {
      return '${(bytes / (1024 * 1024)).toStringAsFixed(1)} MB';
    }
  }

  // Format duration to MM:SS
  String _formatDuration(Duration duration) {
    String twoDigits(int n) => n.toString().padLeft(2, '0');
    final minutes = twoDigits(duration.inMinutes.remainder(60));
    final seconds = twoDigits(duration.inSeconds.remainder(60));
    return '$minutes:$seconds';
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 4,
      margin: const EdgeInsets.all(16),
      child: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          children: [
            // Song Icon
            const Icon(Icons.music_note, size: 100, color: Colors.blue),
            const SizedBox(height: 20),

            // Song Title
            Text(
              songTitle,
              style: Theme.of(context).textTheme.headlineSmall,
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 8),

            // Artist Name
            Text(
              artistName,
              style: Theme.of(context).textTheme.titleMedium,
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 20),

            // Download Progress (text only)
            if (downloadProgress != null) ...[
              Text(
                'Downloaded: ${_formatBytes(downloadProgress!.bytesDownloaded)}',
                style: const TextStyle(
                  fontSize: 12,
                  color: Colors.green,
                  fontWeight: FontWeight.w500,
                ),
              ),
              const SizedBox(height: 16),
            ],

            // Playback Progress Section
            const Text(
              'Playback Progress',
              style: TextStyle(fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            Slider(
              value: totalDuration.inSeconds > 0
                  ? currentPosition.inSeconds.toDouble()
                  : 0.0,
              min: 0.0,
              max: totalDuration.inSeconds > 0
                  ? totalDuration.inSeconds.toDouble()
                  : 1.0,
              onChanged: (value) {
                onSeek(Duration(seconds: value.toInt()));
              },
            ),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16.0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    _formatDuration(currentPosition),
                    style: const TextStyle(fontSize: 12, color: Colors.grey),
                  ),
                  Text(
                    _formatDuration(totalDuration),
                    style: const TextStyle(fontSize: 12, color: Colors.grey),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 20),

            // Play/Pause Controls
            if (isLoading)
              const CircularProgressIndicator()
            else
              IconButton(
                iconSize: 64,
                icon: Icon(
                  isPlaying
                      ? Icons.pause_circle_filled
                      : Icons.play_circle_filled,
                ),
                onPressed: onPlayPause,
              ),
          ],
        ),
      ),
    );
  }
}
