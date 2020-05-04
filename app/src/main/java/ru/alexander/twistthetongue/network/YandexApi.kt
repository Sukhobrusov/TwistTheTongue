package ru.alexander.twistthetongue.network

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query


data class Response(val result: String)

interface YandexApi {
    @POST("/speech/v1/stt:recognize")
    fun recognize(
        @Header("Authorization") authToken: String,
        @Body fileArray: ByteArray,
        @Query("lang") language: String = "ru-RU"
    ): Observable<Response>

    companion object {
        fun create() : YandexApi{
            val retrofit = Retrofit.Builder()
                .baseUrl("https://stt.api.cloud.yandex.net")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
            return retrofit.create()
        }
    }
}