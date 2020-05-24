package ru.alexander.twistthetongue.network

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import ru.alexander.twistthetongue.model.LocalInfo


data class GoogleResponse(val results: ArrayList<Alternatives>)
data class Alternatives(val transcript : String, val confidence : Double)

interface GoogleApi {

    @POST("/v1/speech:recognize")
    fun recognize(
        @Header("Authorization") authToken: String = "Bearer ${LocalInfo.AUTH_KEY_GOOGLE}}",
        @Header("Content-Type") type : String = "application/json; charset=utf-8",
        @Body data: String
    ): Observable<GoogleResponse>

    companion object {
        fun create() : GoogleApi{
            val retrofit = Retrofit.Builder()
                .baseUrl("https://speech.googleapis.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
            return retrofit.create()
        }
    }
}