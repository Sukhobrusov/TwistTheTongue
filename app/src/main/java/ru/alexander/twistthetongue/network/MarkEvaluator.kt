package ru.alexander.twistthetongue.network

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.alexander.twistthetongue.model.MarkReturn
import java.util.*

class MarkEvaluator {

    val api = YandexApi.create()

    val markReturn: MutableLiveData<MarkReturn> by lazy {
        MutableLiveData<MarkReturn>()
    }

    private var disposable: Disposable? = null

    companion object {
        private const val TAG = "MarkViewModel"
    }

    fun recognize(content: ByteArray, sourcePatter: String) {
        Log.d(TAG, "Observing patter - $sourcePatter")
        disposable =
                //api.recognize("",content)
            Observable.fromCallable {
                // imitating hard work
                Thread.sleep(400)
                Response("Peter piper picked peck pickled peppers a peck of pickled peppers peter piper picked if peter")
            }
                .subscribeOn(Schedulers.io())
                //.observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(
                    {

                        val sourceWords = sourcePatter
                            .replace("\n", " & ")
                            .replace(Regex("[.|!|?]"), "")
                            .split(" ")
                        val recognizedSpeechWords = it.result
                            .split(" ")

                        //comparing both sets
                        val difSet = compareStrings(
                            sourceWords.toLowerCase(),
                            recognizedSpeechWords.toLowerCase()
                        )

                        val newLineCount = sourceWords.count { element -> element == "&" }
                        val correctCount = difSet.count { element -> element == 1 }

                        Log.d(TAG, "Diff - $correctCount")
                        val mark =
                            ((correctCount.toDouble() / (difSet.size - newLineCount)) * 100).toInt()

                        val m = MarkReturn(
                            mark = mark,
                            spannable = toSpannable(sourceWords, difSet)
                        )

                        markReturn.postValue(m)
                    },
                    {
                        markReturn.postValue(
                            MarkReturn(
                                mark = -1,
                                spannable = SpannableStringBuilder()
                            )
                        )
                    }
                )
    }

    fun cancelRequest() {
        disposable?.dispose()
    }

    private fun compareStrings(
        sourceWords: List<String>,
        recognizedSpeechWords: List<String>
    ): IntArray {
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


        val difSourceArray = IntArray(sourceWords.size) { -1 }
        //val difArray = IntArray(recognizedSpeechWords.size) { -1 }
        val tolerance = 3

        var i = 0
        var j: Int
        var lastSetIndex = -1

        while (i < sourceWords.size) {
            j = lastSetIndex + 1
            if (sourceWords[i] != "&")
                while (j < lastSetIndex + tolerance + 1 && j < recognizedSpeechWords.size) {
                    if (sourceWords[i] == recognizedSpeechWords[j]) {
                        lastSetIndex = j
                        //difArray[lastSetIndex] = 1
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
            if (sourceWords[j] == "&")
                lastSetIndex++
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


    private fun toSpannable(
        sourceArray: List<String>,
        difArray: IntArray
    ): SpannableStringBuilder {
        val spannableString = SpannableStringBuilder()
        sourceArray.forEachIndexed { index, element ->
            when {
                element == "&" -> spannableString.append("\n")
                difArray[index] == -1 -> spannableString.append(
                    sourceArray[index],
                    ForegroundColorSpan(Color.RED),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                else -> spannableString.append(sourceArray[index])
            }
            spannableString.append(" ")

        }
        return spannableString
    }

    // function that turns each element of string list into lowercase version
    private fun List<String>.toLowerCase(): List<String> =
        this.map { it.toLowerCase(Locale.ENGLISH) }
}