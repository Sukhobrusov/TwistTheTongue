package ru.alexander.twistthetongue

import android.util.Log
import org.junit.Test


class MarkTest {
    @Test
    fun testMark() {
        println(compareStrings("Peter Piper picked a peck of pickled peppers", "peter piper picked a peck of pickled peppers"))
    }

    private fun compareStrings(source: String, recognizedSpeech: String): Int {
        val sourceWords = source.toLowerCase().replace('\n',' ').split(" ")
        val recognizedSpeechWords = recognizedSpeech.toLowerCase().split(" ")

        return if (sourceWords.size > recognizedSpeechWords.size) {
            5
        } else {
            compareArrays(sourceWords, recognizedSpeechWords)
        }

    }


    private fun compareArrays(sourceWords : List<String>, recognizedSpeechWords : List<String>) : Int{

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
        }
        return difArray.count { it == -1 }
    }
}