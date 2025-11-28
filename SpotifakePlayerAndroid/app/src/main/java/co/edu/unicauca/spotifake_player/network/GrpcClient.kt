package co.edu.unicauca.spotifake_player.network

import android.content.Context
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.delay
import java.io.InputStream
import java.io.OutputStream

class GrpcClient(private val context: Context? = null) {
    private var channel: ManagedChannel? = null
    private val chunkSize = 8192

    /**
     * Modified to write to an OutputStream
     */
    suspend fun streamAudio(outputStream: OutputStream) {
        channel = ManagedChannelBuilder
            .forAddress("10.0.2.2", 50051)
            .usePlaintext()
            .build()

        try {
            // TODO: Replace with your actual gRPC implementation
            // Example:
            // val stub = YourServiceGrpc.newBlockingStub(channel)
            // val request = AudioRequest.newBuilder().build()
            // val responseStream = stub.streamAudio(request)
            // responseStream.forEach { response ->
            //     val audioChunk = response.audioData.toByteArray()
            //     outputStream.write(audioChunk)
            // }

            // Simulation: Read from local MP3 file
            simulateStreamingFromFile(outputStream)

        } catch (e: Exception) {
            throw Exception("gRPC streaming error: ${e.message}", e)
        } finally {
            // CRITICAL: Close the output stream when done to signal
            // EOF (End of File) to the reading end (ExoPlayer).
            try {
                outputStream.close()
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    /**
     * Simulates streaming by reading a file and writing it to the OutputStream.
     */
    private suspend fun simulateStreamingFromFile(outputStream: OutputStream) {
        context?.let { ctx ->
            val inputStream: InputStream = try {
                ctx.resources.openRawResource(
                    ctx.resources.getIdentifier("sample_song", "raw", ctx.packageName)
                )
            } catch (e: Exception) {
                throw Exception("MP3 file not found in res/raw/sample_song.mp3", e)
            }

            // Use 'use' blocks for safe stream handling
            inputStream.use { input ->
                outputStream.use { output ->
                    val buffer = ByteArray(chunkSize)
                    var bytesRead: Int
                    var chunkCount = 0

                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        chunkCount++

                        // Simulate network delay
                        if (chunkCount <= 5) {
                            delay(50)
                        } else {
                            delay(150)
                        }
                    }
                }
            }
        } ?: throw Exception("Context is required for file streaming simulation")
    }

    fun close() {
        channel?.shutdown()
        channel = null
    }
}