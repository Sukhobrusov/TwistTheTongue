package ru.alexander.twistthetongue.viewmodels

import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log
import java.io.*


class MediaPlayerViewModel : ViewModel() {

    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null

    companion object {
        private const val LOG_TAG = "MediaPlayer"
    }

    fun startRecording(fileName: String) {
        recorder = MediaRecorder().apply {
            Log.d(LOG_TAG, "Attempting record at $fileName")
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

            try {
                prepare()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }

            start()
        }
    }

    fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }

    fun startPlaying(fileName: String) {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
                prepare()
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
        liveData.postValue(Record(State.CALCULATING, null))
        this.viewModelScope.launch(Dispatchers.IO) {
            val file = File(fileName)
            val size: Int = file.length().toInt()
            val byteArray = ByteArray(size)
            try {
                val buf = BufferedInputStream(FileInputStream(file))
                buf.read(byteArray, 0, byteArray.size)
                buf.close()
                liveData.postValue(
                    Record(State.DONE, byteArray)
                )
            } catch (e: FileNotFoundException) {
                liveData.postValue(
                    Record(State.FILE_NOT_FOUND, null)
                )
                e.printStackTrace()
            } catch (e: IOException) {
                Record(State.ERROR_READING_FILE, null)
                e.printStackTrace()
            }

        }
    }

    data class Record(val state: State, val byteArray: ByteArray?)
    enum class State {
        CALCULATING,
        FILE_NOT_FOUND,
        ERROR_READING_FILE,
        DONE
    }

}