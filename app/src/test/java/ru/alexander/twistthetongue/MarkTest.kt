package ru.alexander.twistthetongue

import org.junit.Test


class MarkTest {
    @Test
    fun testMark() {
        println(
            compareStrings(
                "Peter Piper picked a peck of pickled peppers",
                "Peter picked a peck of pickled peppers"
            ).displayArray()
        )
    }

    private fun compareStrings(source: String, recognizedSpeech: String): IntArray {
        val sourceWords = source.toLowerCase().replace('\n', ' ').split(" ")
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

    fun IntArray.displayArray() : String {
        var string = ""
        this.forEach { string +=  "$it " }
        return string
    }
}