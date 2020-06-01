package ru.alexander.twistthetongue.viewmodels

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Base64
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.alexander.twistthetongue.audio.VoiceRecorder

import java.io.*


class MediaPlayerViewModel : ViewModel(), VoiceRecorder.Callback {

    private var bufferSize = 1024

    private var recorder: MediaRecorder? = null
    private var voiceRecorder : VoiceRecorder? = null
    private var player: MediaPlayer? = null

    private lateinit var outputStream : ByteArrayOutputStream
    private lateinit var fileName : String

    companion object {
        private const val LOG_TAG = "MediaPlayer"
    }

    fun startRecording(fileName: String) {
        this.fileName = fileName
        recorder = createMediaRecorder().apply {
            try {
                //prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }

           // start()
        }

        //voiceRecorder = VoiceRecorder(this)
        //voiceRecorder?.start()
    }

    fun stopRecording() {
        recorder?.apply {
            //stop()
            //reset()
            //release()
        }

        voiceRecorder?.apply {
            //stop()
            //dismiss()
            //voiceRecorder = null
        }
        recorder = null
        voiceRecorder = null
    }

    fun startPlaying(fileName: String, onEndPlaying : () -> Unit) {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
                setOnCompletionListener { onEndPlaying() }
                start()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }

    }

    fun stopPlaying() {
        player?.release()
        player = null
    }

    fun getFileInfo(
        fileName: String,
        liveData: MutableLiveData<Record>
    ) {
        liveData.postValue(Record(State.CALCULATING, ""))
        this.viewModelScope.launch(Dispatchers.IO) {
            val file = File(fileName)
            try {
                val content = Base64.encodeToString(file.readBytes(), Base64.CRLF)
                Log.d(LOG_TAG, content)
                liveData.postValue(
                    Record(State.DONE, content)
                )
            } catch (e: FileNotFoundException) {
                liveData.postValue(
                    Record(State.FILE_NOT_FOUND, "")
                )
                e.printStackTrace()
            } catch (e: IOException) {
                Record(State.ERROR_READING_FILE, "")
                e.printStackTrace()
            }

        }
    }

    private fun putBytesIntoFile() {
        this.viewModelScope.launch(Dispatchers.IO) {
            val file = File(fileName)
            try {
                outputStream.writeTo(file.outputStream())
                outputStream.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    data class Record(val state: State, val encodedString: String)
    enum class State {
        CALCULATING,
        FILE_NOT_FOUND,
        ERROR_READING_FILE,
        DONE
    }

    override fun onVoice(data: ByteArray?, size: Int) {
        data ?: return
        outputStream.write(data)
        Log.d(LOG_TAG, size.toString())
    }

    override fun onVoiceEnd() {
        putBytesIntoFile()
    }

    override fun onVoiceStart() {
        outputStream = ByteArrayOutputStream()
    }

    override fun onCleared() {
        outputStream = ByteArrayOutputStream()
    }


    private fun createMediaRecorder() : MediaRecorder =
        MediaRecorder().apply {
            Log.d(LOG_TAG, "Attempting record at $fileName")
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AMR_WB)
            setAudioSamplingRate(16000)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
        }

}