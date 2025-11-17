package co.edu.unicauca.spotifake_player.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.unicauca.spotifake_player.viewmodel.StreamingPlayerViewModel

@Composable
fun StreamingPlayerScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val viewModel = remember { StreamingPlayerViewModel(context) }

    val downloadStatus by viewModel.downloadStatus.collectAsState(initial = "")
    val playingStatus by viewModel.playingStatus.collectAsState(initial = "")
    val isPlaying by viewModel.isPlaying.collectAsState(initial = false)
    val isPaused by viewModel.isPaused.collectAsState(initial = false)
    val isDownloading by viewModel.isDownloading.collectAsState(initial = false)
    val downloadProgress by viewModel.downloadProgress.collectAsState(initial = 0L)
    val currentPosition by viewModel.currentPosition.collectAsState(initial = 0)
    val duration by viewModel.duration.collectAsState(initial = 0)

    DisposableEffect(Unit) {
        onDispose {
            viewModel.cleanup()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Streaming MP3 Player",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    when {
                        !isDownloading && !isPlaying -> viewModel.startDownloadAndPlay()
                        isPlaying -> viewModel.pausePlayback()
                        isPaused -> viewModel.resumePlayback()
                    }
                },
                modifier = Modifier
                    .width(120.dp)
                    .height(60.dp)
            ) {
                Text(
                    text = when {
                        isPlaying -> "Pause"
                        isPaused -> "Play"
                        else -> "Play"
                    },
                    fontSize = 18.sp
                )
            }

            Button(
                onClick = { viewModel.stopPlayback() },
                modifier = Modifier
                    .width(120.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = "Stop",
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Direct file playback button
        Button(
            onClick = { viewModel.playDirectFromFile() },
            modifier = Modifier
                .width(256.dp)
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text(
                text = "Play Direct (No Stream)",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        StatusPanel(
            downloadStatus = downloadStatus,
            playingStatus = playingStatus,
            downloadProgress = downloadProgress,
            currentPosition = currentPosition,
            duration = duration
        )
    }
}

@Composable
private fun StatusPanel(
    downloadStatus: String,
    playingStatus: String,
    downloadProgress: Long,
    currentPosition: Int,
    duration: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF0F0F0))
            .padding(16.dp)
    ) {
        // Download Status
        Text(
            text = "Download Status:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = downloadStatus,
                fontSize = 16.sp,
                color = Color(0xFF2196F3)
            )

            Text(
                text = formatBytes(downloadProgress),
                fontSize = 16.sp,
                color = Color(0xFF2196F3)
            )
        }

        if (downloadProgress > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { 1f }, // Indeterminate since we don't know total size
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = Color(0xFF2196F3)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Playing Status
        Text(
            text = "Playing Status:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = playingStatus,
            fontSize = 16.sp,
            color = Color(0xFF4CAF50)
        )

        // Playback Progress
        if (duration > 0) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(currentPosition),
                    fontSize = 14.sp,
                    color = Color(0xFF4CAF50)
                )

                Text(
                    text = formatTime(duration),
                    fontSize = 14.sp,
                    color = Color(0xFF4CAF50)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = Color(0xFF4CAF50)
            )
        }
    }
}

private fun formatBytes(bytes: Long): String {
    return when {
        bytes >= 1_000_000 -> String.format("%.2f MB", bytes / 1_000_000.0)
        bytes >= 1_000 -> String.format("%.2f KB", bytes / 1_000.0)
        else -> "$bytes B"
    }
}

private fun formatTime(milliseconds: Int): String {
    val seconds = milliseconds / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%d:%02d", minutes, remainingSeconds)
}