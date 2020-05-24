package ru.alexander.twistthetongue

import android.util.Base64
import org.json.JSONObject
import org.junit.Test
import java.util.*

class JsonTest {
    @Test
    fun testRequest() {
        val byte = java.util.Base64.getEncoder().encode(byteArrayOf(0,0,11))
        print(String(byte))


    }
}