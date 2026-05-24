package com.eltex.androidschool.feauture.event.data

import com.eltex.androidschool.BuildConfig
import com.eltex.androidschool.feauture.event.domain.Callback
import com.eltex.androidschool.feauture.event.domain.Event
import com.eltex.androidschool.feauture.event.domain.EventRepository
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import java.time.Instant
import java.util.concurrent.TimeUnit

class EventRepositoryImpl : EventRepository {
    val json = Json {
        ignoreUnknownKeys = true
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        })
        .build()

    override fun getEvents(callback: Callback<List<Event>>) {
        client.newCall(
            Request.Builder()
                .url("https://eltex-android.ru/api/events")
                .header("Api-Key", BuildConfig.API_KEY)
                .header("Authorization", BuildConfig.Authorization)
                .build()
        )
            .enqueue(
                object : okhttp3.Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(RuntimeException(e))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            callback.onSuccess(
                                json.decodeFromString<List<EventDto>>(response.body.string()).map {
                                    it.toEvent()
                                }
                            )
                        } else {
                            callback.onError(RuntimeException(response.body.string()))
                        }
                    }
                }
            )
    }

    override fun likeById(id: Long, likedByMe: Boolean, callback: Callback<Event>) {
        client.newCall(
            Request.Builder()
                .url("https://eltex-android.ru/api/events/$id/likes")
                .apply { if (likedByMe) delete() else post(RequestBody.EMPTY) }
                .header("Api-Key", BuildConfig.API_KEY)
                .header("Authorization", BuildConfig.Authorization)
                .build()
        )
            .enqueue(
                object : okhttp3.Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(RuntimeException(e))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            callback.onSuccess(
                                json.decodeFromString<EventDto>(response.body.string()).toEvent()
                            )
                        } else {
                            callback.onError(RuntimeException(response.body.string()))
                        }
                    }
                }
            )
    }

    override fun participateById(id: Long, participatedByMe: Boolean, callback: Callback<Event>) {
        client.newCall(
            Request.Builder()
                .url("https://eltex-android.ru/api/events/$id/participants")
                .apply { if (participatedByMe) delete() else post(RequestBody.EMPTY) }
                .header("Api-Key", BuildConfig.API_KEY)
                .header("Authorization", BuildConfig.Authorization)
                .build()
        )
            .enqueue(
                object : okhttp3.Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(RuntimeException(e))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            callback.onSuccess(
                                json.decodeFromString<EventDto>(response.body.string()).toEvent()
                            )
                        } else {
                            callback.onError(RuntimeException(response.body.string()))
                        }
                    }
                }
            )
    }

    override fun saveEvent(id: Long, content: String, callback: Callback<Event>) {
        client.newCall(
            Request.Builder()
                .url("https://eltex-android.ru/api/events")
                .post(
                    json.encodeToString(EventDto(id = id, content = content, datetime = Instant.now().toString()))
                        .toRequestBody("application/json".toMediaType())
                )
                .header("Api-Key", BuildConfig.API_KEY)
                .header("Authorization", BuildConfig.Authorization)
                .build()
        )
            .enqueue(
                object : okhttp3.Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(RuntimeException(e))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            callback.onSuccess(
                                json.decodeFromString<EventDto>(response.body.string()).toEvent()
                            )
                        } else {
                            callback.onError(RuntimeException(response.body.string()))
                        }
                    }
                }
            )
    }

    override fun deleteById(id: Long, callback: Callback<Unit>) {
        client.newCall(
            Request.Builder()
                .url("https://eltex-android.ru/api/events/$id")
                .delete()
                .header("Api-Key", BuildConfig.API_KEY)
                .header("Authorization", BuildConfig.Authorization)
                .build()
        )
            .enqueue(
                object : okhttp3.Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        callback.onError(RuntimeException(e))
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            callback.onSuccess(Unit)
                        } else {
                            callback.onError(RuntimeException(response.body.string()))
                        }
                    }
                }
            )
    }
}