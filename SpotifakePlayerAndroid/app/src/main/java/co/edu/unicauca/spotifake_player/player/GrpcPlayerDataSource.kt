package co.edu.unicauca.spotifake_player.player

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.BaseDataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DataSource
import java.io.IOException
import java.io.InputStream

/**
 * A custom ExoPlayer DataSource that reads from an InputStream.
 * We will use a PipedInputStream to connect our gRPC stream to this.
 */
@UnstableApi
class GrpcDataSource(private val inputStream: InputStream) : BaseDataSource(true) {

    private var bytesRead: Long = 0
    private var uri: Uri? = null
    private var dataSpec: DataSpec? = null // Store the dataSpec

    class Factory(private val inputStream: InputStream) : DataSource.Factory {
        override fun createDataSource(): DataSource {
            return GrpcDataSource(inputStream)
        }
    }

    override fun open(dataSpec: DataSpec): Long {
        this.dataSpec = dataSpec // Store it
        this.uri = dataSpec.uri
        this.bytesRead = 0

        // *** THIS IS THE FIX ***
        // We must call this to notify listeners (like the bandwidth meter)
        // that a transfer is starting.
        transferStarted(dataSpec)

        // We don't know the length, so we return C.LENGTH_UNSET
        return -1L // C.LENGTH_UNSET
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (length == 0) {
            return 0
        }

        try {
            val bytesReadNow = inputStream.read(buffer, offset, length)

            if (bytesReadNow == -1) {
                // End of stream
                return -1 // C.RESULT_END_OF_INPUT
            }

            bytesRead += bytesReadNow
            // Now this call is safe, because the listener
            // received the dataSpec in open()
            bytesTransferred(bytesReadNow)
            return bytesReadNow

        } catch (e: IOException) {
            // This will happen if the pipe is closed
            throw IOException(e)
        }
    }

    override fun getUri(): Uri? {
        return uri
    }

    override fun close() {
        try {
            inputStream.close()
        } catch (_: IOException) {
            // Ignore
        } finally {
            // *** THIS IS THE SECOND FIX ***
            // Notify listeners that the transfer has ended.
            // We check dataSpec != null just in case open() was never called.
            if (dataSpec != null) {
                transferEnded()
            }
            dataSpec = null
            uri = null
        }
    }
}