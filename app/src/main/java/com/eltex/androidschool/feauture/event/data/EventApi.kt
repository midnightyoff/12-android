package com.eltex.androidschool.feauture.event.data

import com.eltex.androidschool.data.RetrofitFactory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EventApi {
    @GET("events")
    fun getAll(): Single<List<EventDto>>

    @POST("events")
    fun saveEvent(@Body event: EventDto): Single<EventDto>

    @POST("events/{id}/likes")
    fun likeById(@Path("id") id: Long): Single<EventDto>

    @DELETE("events/{id}/likes")
    fun dislikeById(@Path("id") id: Long): Single<EventDto>

    @POST("events/{id}/participants")
    fun participateById(@Path("id") id: Long): Single<EventDto>

    @DELETE("events/{id}/participants")
    fun unparticipateById(@Path("id") id: Long): Single<EventDto>

    @DELETE("events/{id}")
    fun deleteById(@Path("id") id: Long): Completable

    companion object {
        val value: EventApi by lazy {
            RetrofitFactory.retrofit.create()
        }
    }
}