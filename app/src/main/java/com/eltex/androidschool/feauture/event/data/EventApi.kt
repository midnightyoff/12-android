package com.eltex.androidschool.feauture.event.data

import com.eltex.androidschool.data.RetrofitFactory
import retrofit2.Call
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface EventApi {
    @GET("events")
    fun getAll(): Call<List<EventDto>>

    @POST("events")
    fun saveEvent(@Body event: EventDto): Call<EventDto>

    @POST("events/{id}/likes")
    fun likeById(@Path("id") id: Long): Call<EventDto>

    @DELETE("events/{id}/likes")
    fun dislikeById(@Path("id") id: Long): Call<EventDto>

    @POST("events/{id}/participants")
    fun participateById(@Path("id") id: Long): Call<EventDto>

    @DELETE("events/{id}/participants")
    fun unparticipateById(@Path("id") id: Long): Call<EventDto>

    @DELETE("events/{id}")
    fun deleteById(@Path("id") id: Long): Call<Unit>

    companion object {
        val value: EventApi by lazy {
            RetrofitFactory.retrofit.create()
        }
    }
}
