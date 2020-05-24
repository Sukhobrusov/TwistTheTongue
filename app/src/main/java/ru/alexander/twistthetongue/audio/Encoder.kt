package ru.alexander.twistthetongue.audio

import android.media.MediaCodec
import android.media.MediaCodecList
import android.media.MediaFormat
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class Encoder(val buffer : ByteArray,val fileName : String) : MediaCodec.Callback() {


    var codec: MediaCodec? = null
    val bufferSize = 1024
    val sampleRate = 16000


    companion object {
        const val LOG_TAG = "Encoder"
    }

    init {
        createAudioEncoder()
    }


    private fun createAudioEncoder() {
        val mcl = MediaCodecList(MediaCodecList.REGULAR_CODECS)

        val format = MediaFormat()
        format.setString(MediaFormat.KEY_MIME, "audio/flac")
        format.setInteger(MediaFormat.KEY_BIT_RATE, 64000)
        format.setInteger(MediaFormat.KEY_SAMPLE_RATE, sampleRate)
        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1)

        val codecName = mcl.findEncoderForFormat(format)
        Log.w(LOG_TAG, codecName)

        try {
            codec = MediaCodec.createByCodecName(codecName)
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Error happened, FUCK!", e)
        }
        codec ?: return
        codec?.setCallback(this)
        codec?.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
    }

    fun startEncoding() {
        Log.d(LOG_TAG, "Encoding started")
        codec?.start()

    }

    fun stopEncoding() {
        Log.d(LOG_TAG, "Encoding stoped")
        codec?.stop()
    }


    override fun onOutputBufferAvailable(
        codec: MediaCodec,
        index: Int,
        info: MediaCodec.BufferInfo
    ) {
        Log.d(LOG_TAG, "Writing encoded buffer to file")
        val outputBuffer = codec.getOutputBuffer(index)
        val size = info.size
        val outputByteArray = ByteArray(size)
        outputBuffer?.get(outputByteArray)

        val file = File(fileName)
        file.writeBytes(outputByteArray)
        codec.releaseOutputBuffer(index, false)
    }

    override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
        val inputBuffer = codec.getInputBuffer(index)
        // fill inputBuffer with valid data
        inputBuffer?.clear()
        inputBuffer?.put(buffer)
        Log.d(LOG_TAG, "")

        codec.queueInputBuffer(index,
            0,
            buffer.size,
            0, 0 )
    }

    override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {

    }

    override fun onError(codec: MediaCodec, e: MediaCodec.CodecException) {
        Log.e(LOG_TAG, "Error :(", e)
    }
}