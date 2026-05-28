package com.eltex.androidschool.data

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitFactory {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://eltex-android.ru/api/")
            .client(OkHttpFactory.client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }
}