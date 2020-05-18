package ru.alexander.twistthetongue.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import ru.alexander.twistthetongue.network.Response
import ru.alexander.twistthetongue.network.YandexApi
import java.lang.Exception
import java.util.*

class MarkViewModel : ViewModel() {

    val api = YandexApi.create()

    val mark : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    private var disposable : Disposable? = null


    fun recognize(content : ByteArray, sourcePatter : String){
        disposable =
            //api.recognize("",content)
            Observable.fromCallable {
                Thread.sleep(400)
                Response("Peter piper picked peck pickled")
            }
            .subscribeOn(Schedulers.io())
            //.observeOn(AndroidSchedulers.mainThread())
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    val difSet = compareStrings(sourcePatter, it.result)
                    mark.postValue((difSet.count { it == -1 } / difSet.size) * 100)
                },
                {
                    mark.postValue(-1)
                }
            )
    }

    private fun cancelRequest(){
        disposable?.dispose()
    }

    private fun compareStrings(source: String, recognizedSpeech: String): IntArray {
        val sourceWords = source.toLowerCase(Locale.ENGLISH).replace('\n', ' ').split(" ")
        val recognizedSpeechWords = recognizedSpeech.toLowerCase().split(" ")

        return if (sourceWords.size > recognizedSpeechWords.size) {
            compareArraysPartially(sourceWords, recognizedSpeechWords)
        } else {
            compareArrays(sourceWords, recognizedSpeechWords)
        }

    }

    private fun compareArrays(
        sourceWords: List<String>,
        recognizedSpeechWords: List<String>
    ): IntArray {

        /**
        val dif = recognizedSpeechWords.size - sourceWords.size
        val difArray = Array(sourceWords.size) { -1 }
        val tolerance = 2

        if (dif < tolerance) {
        var i = 0
        var j = 0
        var shift = 0
        while (i < sourceWords.size && i + shift + j < recognizedSpeechWords.size) {
        if (shift + j < dif)
        if (sourceWords[i] == recognizedSpeechWords[i + shift + j]) {
        difArray[i++] = 1
        shift += j
        j = 0
        } else {
        j++
        }
        else {
        if (sourceWords[i] == recognizedSpeechWords[i + shift + j]) {
        difArray[i] = 1
        shift += j
        } else {
        difArray[i] = -1
        }
        j = 0
        i++
        }
        }
        }**/

        val difSourceArray = IntArray(sourceWords.size) { -1 }
        val difArray = IntArray(recognizedSpeechWords.size) { -1 }
        val tolerance = 3

        var i = 0
        var j: Int
        var lastSetIndex = -1

        while (i < sourceWords.size) {
            j = lastSetIndex + 1
            while (j < lastSetIndex + tolerance + 1 && j < recognizedSpeechWords.size) {
                if (sourceWords[i] == recognizedSpeechWords[j]) {
                    lastSetIndex = j
                    difArray[lastSetIndex] = 1
                    difSourceArray[i] = 1
                    break
                }
                j++
            }
            i++
        }
        return difSourceArray
    }

    private fun compareArraysPartially(
        sourceWords: List<String>,
        recognizedSpeechWords: List<String>
    ): IntArray {

        val difArray = IntArray(sourceWords.size) { -1 }
        val tolerance = 3

        var i = 0
        var j: Int
        var lastSetIndex = -1

        while (i < recognizedSpeechWords.size) {
            j = lastSetIndex + 1
            while (j < lastSetIndex + tolerance + 1 && j < sourceWords.size) {
                if (sourceWords[j] == recognizedSpeechWords[i]) {
                    lastSetIndex = j
                    difArray[lastSetIndex] = 1
                    break
                }
                j++
            }
            i++
        }
        return difArray
    }

}