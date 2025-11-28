package co.edu.unicauca.spotifake_player.repository

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import co.edu.unicauca.spotifake_player.network.GrpcClient
import co.edu.unicauca.spotifake_player.player.GrpcDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.io.PipedInputStream
import java.io.PipedOutputStream

class AudioStreamRepository(private val context: Context) {
    private val grpcClient = GrpcClient(context)

    // We'll use a Piped stream, which is a standard Java pipe.
    // ExoPlayer reads from the 'readPipe'
    // gRPC writes to the 'writePipe'
    private var readPipe: PipedInputStream? = null
    private var writePipe: PipedOutputStream? = null

    /**
     * Creates a DataSource factory for ExoPlayer.
     * This also sets up the pipe and launches the gRPC stream.
     */
    @OptIn(UnstableApi::class)
    fun getStreamingDataSourceFactory(
        scope: CoroutineScope,
        onBytesWritten: (Long) -> Unit
    ): DataSource.Factory {
        try {
            // Create a new pipe
            // Increase the default buffer size to avoid blocking
            readPipe = PipedInputStream(1024 * 128) // 128KB buffer
            writePipe = PipedOutputStream(readPipe)

            // Launch the gRPC stream in the background
            scope.launch(Dispatchers.IO) {
                var totalBytesWritten = 0L
                try {
                    // This new lambda-based stream is just for progress reporting
                    grpcClient.streamAudio(writePipe!!) { bytes ->
                        // This is a custom OutputStream that reports progress
                        totalBytesWritten += bytes
                        onBytesWritten(totalBytesWritten)
                    }
                } catch (e: Exception) {
                    // Handle stream errors (e.g., player stopped)
                    println("gRPC stream failed: ${e.message}")
                    // Close the pipe to signal error to ExoPlayer
                    writePipe?.close()
                }
            }

            // Return a Factory that creates our custom DataSource
            return GrpcDataSource.Factory(readPipe!!)

        } catch (e: IOException) {
            throw Exception("Failed to create pipe", e)
        }
    }

    // This helper class allows us to intercept writes for progress reporting
    private suspend fun GrpcClient.streamAudio(
        outputStream: OutputStream,
        onBytesWritten: (Int) -> Unit
    ) {
        val progressOutputStream = object : OutputStream() {
            override fun write(b: Int) {
                outputStream.write(b)
                if (b != -1) onBytesWritten(1)
            }

            override fun write(b: ByteArray, off: Int, len: Int) {
                outputStream.write(b, off, len)
                onBytesWritten(len)
            }
        }
        // Call the original streamAudio with our wrapped stream
        streamAudio(progressOutputStream)
    }

    // Unchanged
    suspend fun getDirectFile(): File? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.resources.openRawResource(
                context.resources.getIdentifier("sample_song", "raw", context.packageName)
            )
            val directFile = File.createTempFile("direct_audio", ".mp3", context.cacheDir)
            val outputStream = FileOutputStream(directFile)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            directFile
        } catch (e: Exception) {
            null
        }
    }

    fun cleanup() {
        try {
            readPipe?.close()
            writePipe?.close()
        } catch (e: IOException) {
            // Ignore
        }
        readPipe = null
        writePipe = null
        grpcClient.close()
    }
}