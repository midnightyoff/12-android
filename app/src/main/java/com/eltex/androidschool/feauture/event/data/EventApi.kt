package com.eltex.androidschool.feauture.event.data

import com.eltex.androidschool.data.RetrofitFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EventApi {
    @GET("events")
    suspend fun getAll(): List<EventDto>

    @POST("events")
    suspend fun saveEvent(@Body event: EventDto): EventDto

    @POST("events/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): EventDto

    @DELETE("events/{id}/likes")
    suspend fun dislikeById(@Path("id") id: Long): EventDto

    @POST("events/{id}/participants")
    suspend fun participateById(@Path("id") id: Long): EventDto

    @DELETE("events/{id}/participants")
    suspend fun unparticipateById(@Path("id") id: Long): EventDto

    @DELETE("events/{id}")
    suspend fun deleteById(@Path("id") id: Long)

    companion object {
        val value: EventApi by lazy {
            RetrofitFactory.retrofit.create()
        }
    }
}