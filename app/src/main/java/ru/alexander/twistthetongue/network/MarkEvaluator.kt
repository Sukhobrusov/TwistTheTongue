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
import org.json.JSONObject
import org.json.JSONStringer
import ru.alexander.twistthetongue.R
import ru.alexander.twistthetongue.model.MarkReturn
import java.util.*


class MarkEvaluator {

    //val api = GoogleApi.create()

    val markReturn: MutableLiveData<MarkReturn> by lazy {
        MutableLiveData<MarkReturn>()
    }

    private var disposable: Disposable? = null

    companion object {
        private const val TAG = "MarkEvaluator"
        private const val AUTH_TOKEN =""
    }

    fun recognize(content: String, sourcePatter: String) {
        Log.d(TAG, "Observing patter - $sourcePatter")

        val obj = JSONObject(
            """
                {
                    "config": {
                        "encoding" : "AMR_WB",
                        "sampleRateHertz" : 16000,
                        "languageCode": "ru-RU",
                        "enableWordTimeOffsets": false
                    },
                    "audio": {
                        "content" : "$content"
                    }
                }
            """.trimIndent()
        )
        Log.d(TAG, "Sending request - $obj")
        disposable =
            //api.recognize(data = obj.toString())
                Observable.fromCallable {
                    // imitating hard work
                    Thread.sleep(400)
                    GoogleResponse(arrayListOf(Alternatives("мама мыла Милу Милу мила мыло не любила",1.0)))
                }
                .subscribeOn(Schedulers.io())
                //.observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(
                    {
                        Log.d(TAG, it.results.first().transcript)
                        val sourceWords = sourcePatter
                            .replace("\n", " & ")
                            .replace(Regex("[.|,|!|?|—|\\-]"), "")
                            .split(" ")
                        val recognizedSpeechWords = it.results.first().transcript
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
                        Log.e(TAG, it.toString(), it)
                        Log.d(TAG, it.localizedMessage)

                        markReturn.postValue(
                            MarkReturn(
                                mark = -1,
                                spannable = SpannableStringBuilder()
                            )
                        )
                    }
                )
    }

    fun recognizePatter(resolved : String, sourcePatter: String){
        disposable =
                //api.recognize(data = obj.toString())
            Observable.fromCallable {
                GoogleResponse(arrayListOf(Alternatives(resolved,1.0)))
            }
                .subscribeOn(Schedulers.io())
                //.observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .subscribe(
                    {
                        val sourceWords = sourcePatter
                            .replace(Regex("[\\.|,|\\!|\\?|—|\\-]"), "")
                            .replace("\n", " & ")
                            .split(" ")
                            .filter { elem -> elem != "" }
                        val recognizedSpeechWords = it.results.first().transcript
                            .split(" ")


                        val sourc = sourceWords.joinToString { word -> word}
                        Log.d(TAG, it.results.first().transcript)
                        Log.d(TAG, "Comparing to:\n $sourc")

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
                        Log.e(TAG, it.toString(), it)
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
        disposable = null
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
